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

export function crearServicioMovimientos(dependencias = {}) {
  const ejecutarTransaccion =
    dependencias.conTransaccion ?? conTransaccion;

  const buscarProducto =
    dependencias.buscarProductoPorCodigo ?? buscarProductoPorCodigo;

  const cambiarStock =
    dependencias.actualizarStock ?? actualizarStock;

  const listar =
    dependencias.listarMovimientos ?? listarMovimientos;

  const guardar =
    dependencias.guardarMovimiento ?? guardarMovimiento;

  async function obtenerMovimientos() {
    return await listar();
  }

  async function registrarMovimiento(datos) {
    const {
      codigoProducto,
      tipo,
      cantidad,
      motivo
    } = datos ?? {};

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

    return await ejecutarTransaccion(async (cliente) => {
      const producto = await buscarProducto(
        codigoProducto,
        { cliente, bloquear: true }
      );

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

      const productoActualizado = await cambiarStock(
        codigoProducto,
        nuevoStock,
        cliente
      );

      const movimiento = await guardar({
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

  return {
    obtenerMovimientos,
    registrarMovimiento
  };
}

const servicioReal = crearServicioMovimientos();

export const obtenerMovimientos = servicioReal.obtenerMovimientos;
export const registrarMovimiento = servicioReal.registrarMovimiento;