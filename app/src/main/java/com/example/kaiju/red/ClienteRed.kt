package com.example.kaiju.red

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteRed {

    // Desde el emulador, 10.0.2.2 es "el PC donde corre el emulador".
    // Si pones localhost, apuntaría al propio emulador y no encontraría nada.
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    // "by lazy": se crea solo la primera vez que alguien lo usa
    val productosApi: ProductosApi by lazy {
        retrofit.create(ProductosApi::class.java)
    }

    val movimientosApi: MovimientosApi by lazy {
        retrofit.create(MovimientosApi::class.java)
    }
}