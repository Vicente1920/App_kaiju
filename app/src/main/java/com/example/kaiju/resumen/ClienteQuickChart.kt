package com.example.kaiju.resumen

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteQuickChart {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://quickchart.io/" )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: QuickChartApi by lazy {
        retrofit.create(QuickChartApi::class.java)
    }
}