package com.example.kaiju.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.theme.AgotadoTexto
import com.example.kaiju.ui.theme.StockBajoTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(producto: Producto, onVolver: () -> Unit) {

    // Guía 2 (null safety): talla, color y modelo pueden ser null.
    // ?.let solo crea el dato si NO es null. listOfNotNull descarta los null.
    val datos = listOfNotNull(
        producto.talla?.let { "Talla" to it },
        producto.color?.let { "Color" to it },
        producto.modelo?.let { "Modelo" to it },
        "Precio" to "$${producto.precio.toInt()}"
    )

    val colorStock = when {
        producto.stock == 0 -> AgotadoTexto
        producto.stockBajo -> StockBajoTexto
        else -> Color.Unspecified
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("‹ Volver", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                title = {
                    Column {
                        Text("${producto.codigo} · ${producto.categoria}", style = MaterialTheme.typography.labelMedium)
                        Text(producto.nombre, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tarjeta grande del stock
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Stock disponible", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    text = "${producto.stock} uds",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colorStock
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Stock mínimo", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    text = "${producto.stockMinimo}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                EtiquetaEstado(producto)
                            }
                        }
                    }
                }

                // Tarjetas de datos, de a dos por fila (Guía 3: chunked)
                datos.chunked(2).forEach { fila ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            fila.forEach { (titulo, valor) ->
                                TarjetaDato(titulo, valor, Modifier.weight(1f))
                            }
                            // Si la fila quedó con una sola tarjeta, rellena el hueco
                            if (fila.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Historial de movimientos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Text("Aún no hay movimientos registrados")
                }
            }

            // Botones fijos abajo (todavía no hacen nada)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { }, modifier = Modifier.weight(1f)) {
                    Text("Registrar entrada")
                }
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) {
                    Text("Registrar salida")
                }
            }
        }
    }
}