package com.example.kaiju.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.data.productosDeEjemplo
import com.example.kaiju.ui.theme.StockBajoTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen() {
    val productos = remember { productosDeEjemplo() }

    // Guía 3: filter. Solo los productos que están en o bajo su stock mínimo
    val porReponer = productos.filter { it.stockBajo }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Administrador · 15 de octubre", style = MaterialTheme.typography.labelMedium)
                        Text("Resumen general", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fila con los 3 contadores
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TarjetaDato("Productos", "${productos.size}", Modifier.weight(1f))
                    TarjetaDato("Stock bajo", "${porReponer.size}", Modifier.weight(1f), StockBajoTexto)
                    TarjetaDato("Ventas hoy", "\$184.500", Modifier.weight(1f))
                }
            }

            // Botones de acción (todavía no hacen nada)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

            item {
                Text(
                    text = "Productos por reponer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Reutilizamos la tarjeta del Inventario
            items(porReponer) { producto ->
                TarjetaProducto(producto)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Más vendidos de la semana",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        FilaVenta("Polera Kaiju Negra M", "42 uds")
                        FilaVenta("Gorro Lana Gris", "27 uds")
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaDato(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    colorValor: Color = Color.Unspecified
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = titulo, style = MaterialTheme.typography.labelMedium)
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorValor
            )
        }
    }
}

@Composable
fun FilaVenta(nombre: String, unidades: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = nombre)
        Text(text = unidades, fontWeight = FontWeight.Bold)
    }
}