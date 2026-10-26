package com.example.kaiju.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.kaiju.data.cargarMovimientos
import com.example.kaiju.data.cargarProductos
import com.example.kaiju.data.guardarMovimientos
import com.example.kaiju.data.guardarProductos
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.notificaciones.avisarStockBajo
import com.example.kaiju.validacion.stockResultante
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.graphics.Bitmap

@Composable
fun AppKaiju() {
    val pestanas = listOf("Inicio", "Inventario", "Movimientos", "Ventas")

    var seleccionada by remember { mutableIntStateOf(0) }

    val context = LocalContext.current

    var productos by remember { mutableStateOf(cargarProductos(context)) }
    var movimientos by remember { mutableStateOf(cargarMovimientos(context)) }

    //prepara la ventana que pide el permiso de notificaciones
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    //se ejecuta una vez al abrir la app: pide el permiso (solo hace falta desde Android 13)
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    //null = no hay detalle abierto. Si tiene un producto, se muestra su detalle.
    var productoAbierto by remember { mutableStateOf<Producto?>(null) }
    var fotosProductos by remember {
        mutableStateOf<Map<String, Bitmap>>(emptyMap())
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                pestanas.forEachIndexed { indice, titulo ->
                    NavigationBarItem(
                        selected = seleccionada == indice,
                        onClick = {
                            seleccionada = indice
                            productoAbierto = null
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
            val producto = productoAbierto

            if (producto != null) {
                // El botón "atrás" del teléfono también cierra el detalle
                BackHandler { productoAbierto = null }
                DetalleProductoScreen(
                    producto = producto,
                    movimientos = movimientos.filter {
                        it.codigoProducto == producto.codigo},
                    fotoProducto = fotosProductos[producto.codigo],
                    onFotoTomada = { foto ->
                        fotosProductos = fotosProductos + (producto.codigo to foto)
                    },
                    onVolver = { productoAbierto = null }
                )
            } else {

                when (seleccionada) {
                    0 -> InicioScreen(productos = productos)
                    1 -> InventarioScreen(
                        productos = productos,
                        onProductoClick = { productoAbierto = it }
                    )
                    2 -> MovimientosScreen(
                        productos = productos,
                        onConfirmar = { codigo, tipo, cantidad, motivo ->
                            val anterior = productos.find { it.codigo == codigo }

                            //se crea una lista nueva donde solo cambia el stock
                            //del producto del movimiento
                            productos = productos.map { p ->
                                if (p.codigo == codigo) {
                                    p.copy(stock = stockResultante(p, tipo, cantidad))
                                } else {
                                    p
                                }
                            }
                            guardarProductos(context, productos)

                            // Deja registro del movimiento (trazabilidad)
                            movimientos = movimientos + Movimiento(
                                codigoProducto = codigo,
                                tipo = tipo,
                                cantidad = cantidad,
                                motivo = motivo,
                                fecha = LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                            )
                            guardarMovimientos(context, movimientos)

                            // Avisa solo si antes estaba sobre el mínimo
                            // y ahora quedó en el mínimo o bajo él
                            val actualizado = productos.find { it.codigo == codigo }

                            if (
                                anterior != null &&
                                actualizado != null &&
                                actualizado.stockBajo &&
                                anterior.stock != actualizado.stock
                            ) {
                                avisarStockBajo(context, actualizado)
                            }
                        }
                    )
                    else -> VentasScreen()
                }
            }
        }
    }
}