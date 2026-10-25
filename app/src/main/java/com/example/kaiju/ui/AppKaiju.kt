package com.example.kaiju.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.kaiju.data.productosDeEjemplo
import com.example.kaiju.model.Producto

@Composable
fun AppKaiju() {
    val pestanas = listOf("Inicio", "Inventario", "Movimientos", "Ventas")

    var seleccionada by remember { mutableIntStateOf(0) }

    var productos by remember { mutableStateOf(productosDeEjemplo()) }

    // null = no hay detalle abierto. Si tiene un producto, se muestra su detalle.
    var productoAbierto by remember { mutableStateOf<Producto?>(null) }

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
                        onConfirmar = { _, _, _, _ -> }
                    )
                    else -> PantallaPendiente("Ventas")
                }
            }
        }
    }
}

@Composable
fun PantallaPendiente(titulo: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$titulo (próximamente)", style = MaterialTheme.typography.titleMedium)
    }
}