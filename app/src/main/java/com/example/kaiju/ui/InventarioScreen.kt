package com.example.kaiju.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto
import com.example.kaiju.red.ProductoRequest
import com.example.kaiju.ui.theme.AgotadoFondo
import com.example.kaiju.ui.theme.AgotadoTexto
import com.example.kaiju.ui.theme.DisponibleFondo
import com.example.kaiju.ui.theme.DisponibleTexto
import com.example.kaiju.ui.theme.StockBajoFondo
import com.example.kaiju.ui.theme.StockBajoTexto
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    productos: List<Producto>,
    onProductoClick: (Producto) -> Unit,
    onCrearProducto: (ProductoRequest) -> Unit = {},
    mensajeServidor: String? = null,
    onMensajeMostrado: () -> Unit = {}
) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf("Todos") }
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }

    var codigo by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }
    var stock by rememberSaveable { mutableStateOf("") }
    var stockMinimo by rememberSaveable { mutableStateOf("") }
    var talla by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var modelo by rememberSaveable { mutableStateOf("") }

    var mensajeVisible by rememberSaveable { mutableStateOf<String?>(null) }
    var esConfirmacion by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(mensajeServidor) {
        if (!mensajeServidor.isNullOrBlank()) {
            esConfirmacion = mensajeServidor == "Producto registrado"
            mensajeVisible = if (esConfirmacion) {
                "Producto agregado al inventario"
            } else {
                mensajeServidor
            }

            if (esConfirmacion) {
                mostrarFormulario = false
                codigo = ""
                nombre = ""
                categoria = ""
                precio = ""
                stock = ""
                stockMinimo = ""
                talla = ""
                color = ""
                modelo = ""
            }

            delay(3500)
            mensajeVisible = null
            onMensajeMostrado()
        }
    }

    val filtrados = productos.filter { producto ->
        val coincideTexto =
            producto.nombre.contains(busqueda, ignoreCase = true) ||
                    producto.codigo.contains(busqueda, ignoreCase = true)

        val coincideFiltro = when (filtro) {
            "Stock bajo" -> producto.stockBajo && producto.stock > 0
            "Agotado" -> producto.stock == 0
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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

            Button(
                onClick = {
                    mensajeVisible = null
                    mostrarFormulario = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Agregar producto")
            }

            mensajeVisible?.let { mensaje ->
                val colorFondo = if (esConfirmacion) {
                    Color(0xFFE8F5E9)
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }

                val colorTexto = if (esConfirmacion) {
                    Color(0xFF1B5E20)
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorFondo
                    )
                ) {
                    Text(
                        text = if (esConfirmacion) "✓  $mensaje" else "!  $mensaje",
                        color = colorTexto,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
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
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtrados) { producto ->
                    TarjetaProducto(
                        producto = producto,
                        onClick = { onProductoClick(producto) }
                    )
                }
            }
        }
    }

    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { mostrarFormulario = false },
            title = {
                Text("Agregar producto")
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 440.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Código") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it.filter(Char::isDigit) },
                        label = { Text("Precio en pesos") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it.filter(Char::isDigit) },
                        label = { Text("Stock inicial") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = stockMinimo,
                        onValueChange = { stockMinimo = it.filter(Char::isDigit) },
                        label = { Text("Stock mínimo") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = talla,
                        onValueChange = { talla = it },
                        label = { Text("Talla (opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Color (opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = modelo,
                        onValueChange = { modelo = it },
                        label = { Text("Modelo (opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!mensajeVisible.isNullOrBlank() && !esConfirmacion) {
                        Text(
                            text = mensajeVisible.orEmpty(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val precioNumero = precio.toIntOrNull()
                        val stockNumero = stock.toIntOrNull()
                        val stockMinimoNumero = stockMinimo.toIntOrNull()

                        if (
                            codigo.isBlank() ||
                            nombre.isBlank() ||
                            categoria.isBlank() ||
                            precioNumero == null ||
                            stockNumero == null ||
                            stockMinimoNumero == null
                        ) {
                            esConfirmacion = false
                            mensajeVisible =
                                "Completa código, nombre, categoría, precio, stock y stock mínimo."
                        } else {
                            mensajeVisible = null

                            onCrearProducto(
                                ProductoRequest(
                                    codigo = codigo.trim(),
                                    nombre = nombre.trim(),
                                    categoria = categoria.trim(),
                                    precio = precioNumero,
                                    stock = stockNumero,
                                    stockMinimo = stockMinimoNumero,
                                    talla = talla.trim().ifBlank { null },
                                    color = color.trim().ifBlank { null },
                                    modelo = modelo.trim().ifBlank { null }
                                )
                            )
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarFormulario = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TarjetaProducto(
    producto: Producto,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
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

            Column(
                horizontalAlignment = Alignment.End
            ) {
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

    Surface(
        color = fondo,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
        )
    }
}