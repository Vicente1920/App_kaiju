package com.example.kaiju.red

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteRed {

    //API desplegada en la instancia EC2
    // la barra final es necesaria para retrofit.
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://3.229.68.111:3000/" )
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    // se crea la api cuando se utiliza por primera vez
    // "by lazy": se crea solo la primera vez que alguien lo usa
    val productosApi: ProductosApi by lazy {
        retrofit.create(ProductosApi::class.java)
    }

    val movimientosApi: MovimientosApi by lazy {
        retrofit.create(MovimientosApi::class.java)
    }
}