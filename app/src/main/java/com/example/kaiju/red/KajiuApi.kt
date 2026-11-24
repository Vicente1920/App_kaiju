package com.example.kaiju.red

import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProductosApi {
    @GET("api/productos")
    suspend fun listar(): List<Producto>

    @POST("api/productos")
    suspend fun crear(@Body producto: ProductoRequest): Producto
}

interface MovimientosApi {
    @GET("api/movimientos")
    suspend fun listar(): List<Movimiento>

    @POST("api/movimientos")
    suspend fun registrar(@Body pedido: MovimientoRequest): Movimiento
}

data class MovimientoRequest(
    val codigoProducto: String,
    val tipo: String,
    val cantidad: Int,
    val motivo: String
)

data class ProductoRequest(
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val stockMinimo: Int,
    val talla: String? = null,
    val color: String? = null,
    val modelo: String? = null
)