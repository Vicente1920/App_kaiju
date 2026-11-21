package com.example.kaiju.red

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteRed {

    // Desde el emulador, 10.0.2.2 es "el PC donde corre el emulador".
    // Si pones localhost, apuntaría al propio emulador y no encontraría nada.
    private const val HOST = "http://10.0.2.2"

    // La dirección base SIEMPRE debe terminar en "/"
    private fun crear(puerto: Int): Retrofit = Retrofit.Builder()
        .baseUrl("$HOST:$puerto/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // "by lazy": se crea solo la primera vez que alguien lo usa
    val productosApi: ProductosApi by lazy {
        crear(8081).create(ProductosApi::class.java)
    }

    val movimientosApi: MovimientosApi by lazy {
        crear(8082).create(MovimientosApi::class.java)
    }
}