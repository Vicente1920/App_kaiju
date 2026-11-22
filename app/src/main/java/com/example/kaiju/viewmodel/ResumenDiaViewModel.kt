package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.Movimiento
import com.example.kaiju.resumen.ClienteQuickChart
import com.example.kaiju.resumen.QuickChartConfig
import com.example.kaiju.resumen.QuickChartData
import com.example.kaiju.resumen.QuickChartDataset
import com.example.kaiju.resumen.QuickChartSolicitud
import com.example.kaiju.resumen.contarMovimientosDeHoy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadoResumenDia(
    val cargando: Boolean = false,
    val entradas: Int = 0,
    val salidas: Int = 0,
    val imagenGrafico: ByteArray? = null,
    val error: String? = null
)

class ResumenDiaViewModel : ViewModel() {

    private val _estado = MutableStateFlow(EstadoResumenDia())
    val estado: StateFlow<EstadoResumenDia> = _estado.asStateFlow()

    fun actualizar(movimientos: List<Movimiento>) {
        viewModelScope.launch {
            val conteo = contarMovimientosDeHoy(movimientos)

            _estado.value = EstadoResumenDia(
                cargando = true,
                entradas = conteo.entradas,
                salidas = conteo.salidas
            )

            try {
                val solicitud = QuickChartSolicitud(
                    chart = QuickChartConfig(
                        data = QuickChartData(
                            labels = listOf("Entradas", "Salidas"),
                            datasets = listOf(
                                QuickChartDataset(
                                    label = "Movimientos de hoy",
                                    data = listOf(
                                        conteo.entradas,
                                        conteo.salidas
                                    ),
                                    backgroundColor = listOf(
                                        "#2E7D32",
                                        "#C62828"
                                    )
                                )
                            )
                        )
                    )
                )

                val respuesta = ClienteQuickChart.api.crearGrafico(solicitud)

                if (!respuesta.isSuccessful) {
                    throw Exception("QuickChart respondió con HTTP ${respuesta.code()}")
                }

                val bytes = respuesta.body()?.bytes()

                if (bytes == null || bytes.isEmpty()) {
                    throw Exception("QuickChart no devolvió una imagen")
                }

                _estado.value = EstadoResumenDia(
                    entradas = conteo.entradas,
                    salidas = conteo.salidas,
                    imagenGrafico = bytes
                )
            } catch (e: Exception) {
                _estado.value = EstadoResumenDia(
                    entradas = conteo.entradas,
                    salidas = conteo.salidas,
                    error = "No se pudo generar el gráfico"
                )
            }
        }
    }
}