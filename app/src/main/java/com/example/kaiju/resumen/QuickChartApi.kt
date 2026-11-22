package com.example.kaiju.resumen

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QuickChartApi {

    @POST("chart")
    suspend fun crearGrafico(
        @Body solicitud: QuickChartSolicitud
    ): Response<ResponseBody>
}