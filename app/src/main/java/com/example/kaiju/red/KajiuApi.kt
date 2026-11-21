package com.example.kaiju.red



import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Habla con productos-service (puerto 8081)
interface ProductosApi {
    @GET("api/productos")
    suspend fun listar(): List<Producto>
}

// Habla con movimientos-service (puerto 8082)
interface MovimientosApi {
    @GET("api/movimientos")
    suspend fun listar(): List<Movimiento>

    @POST("api/movimientos")
    suspend fun registrar(@Body pedido: MovimientoRequest): Movimiento
}

// Lo que la app le manda al servidor al registrar un movimiento.
// El id y la fecha los pone el servidor, por eso no están aquí.
data class MovimientoRequest(
    val codigoProducto: String,
    val tipo: String,
    val cantidad: Int,
    val motivo: String
)