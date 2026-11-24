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
import com.example.kaiju.red.ProductoRequest
import com.example.kaiju.validacion.stockResultante
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio =
        InventarioRepository(KaijuDatabase.obtener(application))

    // Room emite una lista nueva cada vez que cambia la tabla.
    // La pantalla siempre lee de Room; el servidor solo la mantiene actualizada.
    val productos: StateFlow<List<Producto>> = repositorio.productos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val movimientos: StateFlow<List<Movimiento>> = repositorio.movimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Fotos por código de producto; se mantienen en memoria.
    private val _fotos = MutableStateFlow<Map<String, Bitmap>>(emptyMap())
    val fotos: StateFlow<Map<String, Bitmap>> = _fotos.asStateFlow()

    // Mensajes para el usuario. null = no hay nada que mostrar.
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    init {
        viewModelScope.launch {
            // Primera vez sin conexión: al menos hay datos de ejemplo para mostrar.
            if (repositorio.contarProductos() == 0) {
                repositorio.insertarProductos(cargarProductos(getApplication()))
            }

            // Después se intenta bajar los datos reales del servidor.
            sincronizarAhora()
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            sincronizarAhora()
        }
    }

    // Devuelve true si pudo bajar los datos. Si falla, Room queda como estaba.
    private suspend fun sincronizarAhora(): Boolean {
        return try {
            repositorio.sincronizar()
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _mensaje.value = "Sin conexión: se muestran los últimos datos guardados"
            false
        }
    }

    fun registrarMovimiento(
        codigo: String,
        tipo: String,
        cantidad: Int,
        motivo: String
    ) {
        viewModelScope.launch {
            val anterior = repositorio.obtenerProducto(codigo) ?: return@launch

            // Defensa local: evita enviar al servidor una salida que ya sabemos que falla.
            if (stockResultante(anterior, tipo, cantidad) < 0) {
                _mensaje.value =
                    "No hay stock suficiente (disponible: ${anterior.stock})"
                return@launch
            }

            // El servidor valida y registra el movimiento.
            try {
                repositorio.registrarEnServidor(codigo, tipo, cantidad, motivo)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = textoDeError(e)
                return@launch
            }

            // Si se guardó en el servidor, se actualizan los datos locales desde la API.
            if (!sincronizarAhora()) return@launch
            _mensaje.value = "Movimiento registrado"

            // Avisa solo si el stock bajó y quedó en nivel bajo.
            val actualizado = repositorio.obtenerProducto(codigo) ?: return@launch
            if (actualizado.stockBajo && actualizado.stock < anterior.stock) {
                avisarStockBajo(getApplication(), actualizado)
            }
        }
    }

    fun crearProducto(producto: ProductoRequest) {
        viewModelScope.launch {
            try {
                repositorio.crearProductoEnServidor(producto)
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                _mensaje.value = when (e.code()) {
                    400 -> "Revisa los datos del producto"
                    409 -> "Ya existe un producto con ese código"
                    else -> "El servidor no pudo guardar el producto"
                }
                return@launch
            } catch (e: IOException) {
                _mensaje.value =
                    "Sin conexión con el servidor; el producto no se pudo guardar"
                return@launch
            } catch (e: Exception) {
                _mensaje.value = "Error inesperado al guardar el producto"
                return@launch
            }

            if (!sincronizarAhora()) {
                _mensaje.value =
                    "Producto guardado, pero no se pudo actualizar la lista. Intenta sincronizar."
                return@launch
            }

            _mensaje.value = "Producto registrado"
        }
    }

    // Traduce errores técnicos a mensajes que entienda el usuario.
    private fun textoDeError(e: Exception): String = when {
        e is HttpException && e.code() == 409 ->
            "No hay stock suficiente"

        e is HttpException && e.code() == 404 ->
            "El producto no existe en el servidor"

        e is HttpException && e.code() == 400 ->
            "Datos inválidos: revisa cantidad y motivo"

        e is HttpException && e.code() == 503 ->
            "El servicio de productos no está disponible"

        e is IOException ->
            "Sin conexión con el servidor"

        else ->
            "Error inesperado"
    }

    fun guardarFoto(codigo: String, foto: Bitmap) {
        _fotos.value = _fotos.value + (codigo to foto)
    }
}