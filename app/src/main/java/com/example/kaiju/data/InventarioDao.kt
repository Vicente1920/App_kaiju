package com.example.kaiju.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface InventarioDao {

    // Flow: Room avisa solo cada vez que la tabla cambia
    @Query("SELECT * FROM productos")
    fun observarProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM movimientos ORDER BY id")
    fun observarMovimientos(): Flow<List<Movimiento>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int

    @Query("SELECT * FROM productos WHERE codigo = :codigo")
    suspend fun obtenerProducto(codigo: String): Producto?

    // REPLACE: si ya existe un producto con ese código, lo reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<Producto>)

    @Insert
    suspend fun insertarMovimiento(movimiento: Movimiento)

    @Update
    suspend fun actualizarProducto(producto: Producto)
}