package com.example.kaiju.data

import androidx.room.withTransaction
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.red.ClienteRed
import com.example.kaiju.red.MovimientoRequest
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

    // Actualiza el stock y deja el movimiento, todo o nada (modo local)
    suspend fun aplicarMovimiento(producto: Producto, movimiento: Movimiento) {
        db.withTransaction {
            dao.actualizarProducto(producto)
            dao.insertarMovimiento(movimiento)
        }
    }

    // Baja todo del servidor y reemplaza la copia local (Room).
    // Si no hay conexión, las dos primeras líneas lanzan una excepción
    // y Room queda intacto, así la app sigue mostrando lo último que bajó.
    suspend fun sincronizar() {
        val productosServidor = ClienteRed.productosApi.listar()
        val movimientosServidor = ClienteRed.movimientosApi.listar()

        db.withTransaction {
            dao.borrarProductos()
            dao.borrarMovimientos()
            dao.insertarProductos(productosServidor)
            dao.insertarMovimientos(movimientosServidor)
        }
    }

    // Manda el movimiento al servidor. El servidor valida y cambia el stock.
    suspend fun registrarEnServidor(codigo: String, tipo: String, cantidad: Int, motivo: String) {
        ClienteRed.movimientosApi.registrar(
            MovimientoRequest(
                codigoProducto = codigo,
                tipo = tipo,
                cantidad = cantidad,
                motivo = motivo
            )
        )
    }
}