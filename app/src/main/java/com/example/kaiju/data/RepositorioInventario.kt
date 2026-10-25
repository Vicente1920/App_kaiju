package com.example.kaiju.data


import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import kotlin.collections.filter

class RepositorioInventario {

    private var productos: List<Producto> = productosDeEjemplo()
    private var movimientos: List<Movimiento> = emptyList()

    fun obtenerProductos(): List<Producto> = productos

    fun obtenerMovimientos(codigo: String): List<Movimiento> =
        movimientos.filter { it.codigoProducto == codigo }

    fun obtenerTodosLosMovimientos(): List<Movimiento> = movimientos

    fun registrarMovimiento(movimiento: Movimiento) {
        productos = productos.map { producto ->
            if (producto.codigo == movimiento.codigoProducto) {
                val nuevoStock = when (movimiento.tipo) {
                    "Salidas" -> producto.stock - movimiento.cantidad
                    else -> producto.stock + movimiento.cantidad
                }
                producto.copy(stock = nuevoStock)
            } else {
                producto
            }
        }
        movimientos = movimientos + movimiento
    }
}