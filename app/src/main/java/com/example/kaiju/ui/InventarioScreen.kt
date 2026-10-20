package com.example.kaiju.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.data.productosDeEjemplo
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.theme.AgotadoFondo
import com.example.kaiju.ui.theme.AgotadoTexto
import com.example.kaiju.ui.theme.DisponibleFondo
import com.example.kaiju.ui.theme.DisponibleTexto
import com.example.kaiju.ui.theme.StockBajoFondo
import com.example.kaiju.ui.theme.StockBajoTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(onProductoClick: (Producto) -> Unit) {
    val productos = remember { productosDeEjemplo() }

    var busqueda by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }

    val filtrados = productos.filter { p ->
        val coincideTexto = p.nombre.contains(busqueda, ignoreCase = true) ||
                p.codigo.contains(busqueda, ignoreCase = true)
        val coincideFiltro = when (filtro) {
            "Stock bajo" -> p.stockBajo && p.stock > 0
            "Agotado" -> p.stock == 0
            else -> true
        }
        coincideTexto && coincideFiltro
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar por nombre o código") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todos", "Stock bajo", "Agotado").forEach { opcion ->
                    FilterChip(
                        selected = filtro == opcion,
                        onClick = { filtro = opcion },
                        label = { Text(opcion) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            if (filtrados.isEmpty()) {
                Text(
                    text = "No hay productos que coincidan",
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtrados) { producto ->
                    TarjetaProducto(producto, onClick = { onProductoClick(producto) })
                }
            }
        }
    }
}

@Composable
fun TarjetaProducto(producto: Producto, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${producto.codigo} · ${producto.categoria} · $${producto.precio.toInt()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${producto.stock}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                EtiquetaEstado(producto)
            }
        }
    }
}

@Composable
fun EtiquetaEstado(producto: Producto) {
    val texto: String
    val fondo: Color
    val colorTexto: Color

    when {
        producto.stock == 0 -> {
            texto = "Agotado"
            fondo = AgotadoFondo
            colorTexto = AgotadoTexto
        }
        producto.stockBajo -> {
            texto = "Stock bajo"
            fondo = StockBajoFondo
            colorTexto = StockBajoTexto
        }
        else -> {
            texto = "Disponible"
            fondo = DisponibleFondo
            colorTexto = DisponibleTexto
        }
    }

    Surface(color = fondo, shape = RoundedCornerShape(50)) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}