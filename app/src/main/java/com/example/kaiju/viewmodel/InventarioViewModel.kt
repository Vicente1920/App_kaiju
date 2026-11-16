package com.example.kaiju.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.data.InventarioRepository
import com.example.kaiju.data.KaijuDatabase
import com.example.kaiju.data.cargarProductos
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.notificaciones.avisarStockBajo
import com.example.kaiju.validacion.stockResultante
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        InventarioRepository(KaijuDatabase.obtener(application))

    // Room emite una lista nueva cada vez que cambia la tabla
    val productos: StateFlow<List<Producto>> = repositorio.productos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val movimientos: StateFlow<List<Movimiento>> = repositorio.movimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // fotos por código de producto (siguen en memoria)
    private val _fotos = MutableStateFlow<Map<String, Bitmap>>(emptyMap())
    val fotos: StateFlow<Map<String, Bitmap>> = _fotos.asStateFlow()

    init {
        // Primera vez: la base está vacía, se cargan los productos iniciales
        viewModelScope.launch {
            if (repositorio.contarProductos() == 0) {
                repositorio.insertarProductos(
                    cargarProductos(getApplication())
                )
            }
        }
    }

    fun registrarMovimiento(codigo: String, tipo: String, cantidad: Int, motivo: String) {
        viewModelScope.launch {
            val anterior = repositorio.obtenerProducto(codigo) ?: return@launch
            val actualizado = anterior.copy(
                stock = stockResultante(anterior, tipo, cantidad)
            )
            val movimiento = Movimiento(
                codigoProducto = codigo,
                tipo = tipo,
                cantidad = cantidad,
                motivo = motivo,
                fecha = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
            )

            repositorio.aplicarMovimiento(actualizado, movimiento)

            if (actualizado.stockBajo && anterior.stock != actualizado.stock) {
                avisarStockBajo(getApplication(), actualizado)
            }
        }
    }

    fun guardarFoto(codigo: String, foto: Bitmap) {
        _fotos.value = _fotos.value + (codigo to foto)
    }
}