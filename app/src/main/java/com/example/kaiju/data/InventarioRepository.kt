package com.example.kaiju.data

import androidx.room.withTransaction
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import kotlinx.coroutines.flow.Flow

class InventarioRepository(private val db: KaijuDatabase) {

    private val dao = db.inventarioDao()

    val productos: Flow<List<Producto>> = dao.observarProductos()
    val movimientos: Flow<List<Movimiento>> = dao.observarMovimientos()

    suspend fun contarProductos(): Int = dao.contarProductos()

    suspend fun obtenerProducto(codigo: String): Producto? =
        dao.obtenerProducto(codigo)

    suspend fun insertarProductos(lista: List<Producto>) =
        dao.insertarProductos(lista)

    // Actualiza el stock y deja el movimiento, todo o nada
    suspend fun aplicarMovimiento(producto: Producto, movimiento: Movimiento) {
        db.withTransaction {
            dao.actualizarProducto(producto)
            dao.insertarMovimiento(movimiento)
        }
    }
}

