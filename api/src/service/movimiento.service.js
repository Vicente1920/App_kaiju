import {
    buscarProductoPorCodigo,
    actualizarStock
} from "../repository/producto.repository.js";

import {
    listarMovimientos,
    guardarMovimiento
} from "../repository/movimiento.repository.js";

export async function obtenerMovimientos() {
    return await listarMovimientos();
}

export async function registrarMovimiento(datos) {
    const {
        codigoProducto,
        tipo,
        cantidad,
        motivo
    } = datos;

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

    const producto = await buscarProductoPorCodigo(codigoProducto);

    if (!producto) {
        return {
            error: "Producto no encontrado",
            codigo: 404
        };
    }

    let nuevoStock;

    if (tipo === "Salida") {
        nuevoStock = producto.stock - cantidad;

        if (nuevoStock < 0) {
            return {
                error: "No hay stock suficiente",
                codigo: 409
            };
        }
    } else if (tipo === "Entrada" || tipo === "Ajuste") {
        nuevoStock = producto.stock + cantidad;
    } else {
        return {
            error: "Tipo de movimiento inválido",
            codigo: 400
        };
    }

    await actualizarStock(codigoProducto, nuevoStock);

    const movimiento = await guardarMovimiento({
        codigoProducto,
        tipo,
        cantidad,
        motivo
    });

    return {
        movimiento,
        producto: await buscarProductoPorCodigo(codigoProducto)
    };
}