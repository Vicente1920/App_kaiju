import test from "node:test";
import assert from "node:assert/strict";
import {
  crearServicioMovimientos,
  registrarMovimiento
} from "../src/service/movimiento.service.js";
test("rechaza datos incompletos con codigo 400", async () => {
  const resultado = await registrarMovimiento({});
  assert.equal(resultado.codigo, 400);
});

test("rechaza cantidad cero con codigo 400", async () => {
  const resultado = await registrarMovimiento({
    codigoProducto: "KJ-001",
    tipo: "Salida",
    cantidad: 0,
    motivo: "Prueba"
  });

  assert.equal(resultado.codigo, 400);
});

test("rechaza tipo invalido con codigo 400", async () => {
  const resultado = await registrarMovimiento({
    codigoProducto: "KJ-001",
    tipo: "Robo",
    cantidad: 1,
    motivo: "Prueba"
  });

  assert.equal(resultado.codigo, 400);
});

function crearServicioDePrueba(producto) {
  return crearServicioMovimientos({
    conTransaccion: async (trabajo) => trabajo({ prueba: true }),
    buscarProductoPorCodigo: async () => producto,
    actualizarStock: async (codigoProducto, stock) => ({
      codigo: codigoProducto,
      stock
    }),
    guardarMovimiento: async (datos) => ({
      id: 1,
      ...datos
    })
  });
}

test("devuelve 404 si el producto no existe", async () => {
  const servicio = crearServicioDePrueba(null);

  const resultado = await servicio.registrarMovimiento({
    codigoProducto: "NO-EXISTE",
    tipo: "Salida",
    cantidad: 1,
    motivo: "Prueba"
  });

  assert.equal(resultado.codigo, 404);
});

test("devuelve 409 si la salida supera el stock", async () => {
  const servicio = crearServicioDePrueba({
    codigo: "KJ-TEST",
    stock: 0
  });

  const resultado = await servicio.registrarMovimiento({
    codigoProducto: "KJ-TEST",
    tipo: "Salida",
    cantidad: 1,
    motivo: "Prueba"
  });

  assert.equal(resultado.codigo, 409);
});

test("registra una entrada válida y aumenta el stock", async () => {
  const servicio = crearServicioDePrueba({
    codigo: "KJ-TEST",
    stock: 5
  });

  const resultado = await servicio.registrarMovimiento({
    codigoProducto: "KJ-TEST",
    tipo: "Entrada",
    cantidad: 2,
    motivo: "Prueba"
  });

  assert.equal(resultado.producto.stock, 7);
  assert.equal(resultado.movimiento.tipo, "Entrada");
});

test("registra una salida válida y reduce el stock", async () => {
  const servicio = crearServicioDePrueba({
    codigo: "KJ-TEST",
    stock: 3
  });

  const resultado = await servicio.registrarMovimiento({
    codigoProducto: "KJ-TEST",
    tipo: "Salida",
    cantidad: 2,
    motivo: "Prueba"
  });

  assert.equal(resultado.producto.stock, 1);
  assert.equal(resultado.movimiento.tipo, "Salida");
});