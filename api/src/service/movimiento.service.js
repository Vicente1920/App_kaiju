import { conTransaccion } from "../config/db.js";

import {
    buscarProductoPorCodigo,
    actualizarStock
} from "../repository/producto.repository.js";

import {
    listarMovimientos,
    guardarMovimiento
} from "../repository/movimiento.repository.js";

const TIPOS_VALIDOS = ["Entrada", "Salida", "Ajuste"];

export async function obtenerMovimientos() {
    return await listarMovimientos();
}

export async function registrarMovimiento(datos) {
    const {
        codigoProducto,
        tipo,
        cantidad,
        motivo
    } = datos ?? {};

    // Validaciones que no necesitan la base de datos
    if (!codigoProducto || !tipo || !motivo || !Number.isInteger(cantidad)) {
        return {
            error: "Los datos del movimiento son inválidos",
            codigo: 400
        };
    }

    if (cantidad <= 0) {
        return {
            error: "La cantidad debe ser mayor que 0",
            codigo: 400
        };
    }

    if (!TIPOS_VALIDOS.includes(tipo)) {
        return {
            error: "Tipo de movimiento inválido",
            codigo: 400
        };
    }

    // Todo lo siguiente ocurre en UNA transacción: o se guarda todo, o no se guarda nada.
    return await conTransaccion(async (cliente) => {
        // Bloquea la fila del producto: otra petición sobre el mismo producto espera su turno.
        const producto = await buscarProductoPorCodigo(codigoProducto, { cliente, bloquear: true });

        if (!producto) {
            return {
                error: "Producto no encontrado",
                codigo: 404
            };
        }

        const nuevoStock = tipo === "Salida"
            ? producto.stock - cantidad
            : producto.stock + cantidad;

        if (nuevoStock < 0) {
            return {
                error: "No hay stock suficiente",
                codigo: 409
            };
        }

        const productoActualizado = await actualizarStock(codigoProducto, nuevoStock, cliente);

        const movimiento = await guardarMovimiento({
            codigoProducto,
            tipo,
            cantidad,
            motivo
        }, cliente);

        return {
            movimiento,
            producto: productoActualizado
        };
    });
}
