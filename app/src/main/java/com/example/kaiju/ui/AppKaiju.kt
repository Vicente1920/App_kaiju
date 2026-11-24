package com.example.kaiju.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kaiju.viewmodel.InventarioViewModel

@Composable
fun AppKaiju() {
    val pestanas = listOf("Inicio", "Inventario", "Movimientos", "Ventas", "Resumen")

    var seleccionada by rememberSaveable { mutableIntStateOf(0) }

    var tipoInicial by rememberSaveable { mutableStateOf("Entrada") }

    // El ViewModel es el dueño de los productos y movimientos
    val viewModel: InventarioViewModel = viewModel()

    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    // Un solo cuadro que pide todos los permisos que la app necesita al abrir
    val pedirPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    // Se ejecuta una vez al abrir la app
    LaunchedEffect(Unit) {
        val lista = mutableListOf<String>()
        // Notificaciones: solo hace falta desde Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            lista.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        // Android 17 (API 37) bloquea la red local si no se pide este permiso
        if (Build.VERSION.SDK_INT >= 37) {
            lista.add("android.permission.ACCESS_LOCAL_NETWORK")
        }
        if (lista.isNotEmpty()) {
            pedirPermisos.launch(lista.toTypedArray())
        }
    }

    // Se guarda solo el CÓDIGO del producto abierto (null = no hay detalle)
    var codigoAbierto by rememberSaveable { mutableStateOf<String?>(null) }

    val fotosProductos by viewModel.fotos.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                pestanas.forEachIndexed { indice, titulo ->
                    NavigationBarItem(
                        selected = seleccionada == indice,
                        onClick = {
                            seleccionada = indice
                            codigoAbierto = null
                        },
                        icon = {},
                        label = {
                            Text(
                                text = titulo,
                                fontWeight = if (seleccionada == indice) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            // ANIMACIÓN: fundido entre pestañas y entre lista y detalle.
            // Dentro del bloque se usan "pestana" y "codigo" (no las variables de arriba),
            // así la pantalla que sale no cambia de contenido a mitad de la animación.
            AnimatedContent(
                targetState = seleccionada to codigoAbierto,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                label = "pantallas"
            ) { (pestana, codigo) ->
                val producto = productos.find { it.codigo == codigo }

                if (producto != null) {
                    // El botón "atrás" del teléfono también cierra el detalle
                    BackHandler { codigoAbierto = null }
                    DetalleProductoScreen(
                        producto = producto,
                        movimientos = movimientos.filter {
                            it.codigoProducto == producto.codigo
                        }.reversed(),
                        fotoProducto = fotosProductos[producto.codigo],
                        mensajeServidor = mensaje,
                        onMensajeMostrado = { viewModel.limpiarMensaje() },
                        onFotoTomada = { foto ->
                            viewModel.guardarFoto(producto.codigo, foto)
                        },
                        onVolver = { codigoAbierto = null },
                        onRegistrarMovimiento = { tipo, cantidad, motivo ->
                            viewModel.registrarMovimiento(producto.codigo, tipo, cantidad, motivo)
                        }
                    )
                } else {
                    when (pestana) {
                        0 -> InicioScreen(
                            productos = productos,
                            onRegistrar = { tipo ->
                                tipoInicial = tipo
                                seleccionada = 2   // salta a la pestaña Movimientos
                            }
                        )
                        1 -> InventarioScreen(
                            productos = productos,
                            onProductoClick = { codigoAbierto = it.codigo },
                            onCrearProducto = { producto ->
                                viewModel.crearProducto(producto)
                            },
                            mensajeServidor = mensaje,
                            onMensajeMostrado = { viewModel.limpiarMensaje() }
                        )
                        2 -> MovimientosScreen(
                            productos = productos,
                            tipoInicial = tipoInicial,
                            mensajeServidor = mensaje,
                            onMensajeMostrado = { viewModel.limpiarMensaje() },
                            onConfirmar = { cod, tipo, cantidad, motivo ->
                                viewModel.registrarMovimiento(cod, tipo, cantidad, motivo)
                            }
                        )
                        3 -> VentasScreen()
                        4 -> ResumenDiaScreen(
                            movimientos = movimientos
                        )
                    }
                }
            }
        }
    }
}