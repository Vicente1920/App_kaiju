package com.example.kaiju.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kaiju.data.ProductoVendido
import com.example.kaiju.data.diasSemana
import com.example.kaiju.data.masVendidos
import com.example.kaiju.data.menosVendidos
import com.example.kaiju.data.ventasPorDia

// Pasa 1284300 a "1.284.300"
private fun formatoPesos(monto: Int): String {
    return "%,d".format(monto).replace(",", ".")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen() {
    val dias = diasSemana()
    val ventas = ventasPorDia()
    val total = ventas.sum()
    val maximo = ventas.max()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ventas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total y gráfico de barras de la semana
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total de la semana")
                    Text("$" + formatoPesos(total), style = MaterialTheme.typography.headlineLarge)

                    // Una barra por día. El alto es proporcional al día que más vendió
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        ventas.forEach { valor ->
                            val alto = 120 * valor / maximo
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(alto.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    // Letras de los días, debajo de cada barra
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dias.forEach { dia ->
                            Text(
                                text = dia,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            ListaVendidos("Más vendidos", masVendidos())
            ListaVendidos("Menos vendidos", menosVendidos())
        }
    }
}

// La misma tarjeta sirve para las dos listas, solo cambia el título y los productos
@Composable
fun ListaVendidos(titulo: String, productos: List<ProductoVendido>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            productos.forEach { producto ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(producto.nombre)
                    Text("${producto.unidades} uds")
                }
            }
        }
    }
}