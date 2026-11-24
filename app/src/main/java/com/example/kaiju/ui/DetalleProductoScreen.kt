package com.example.kaiju.ui

import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.theme.AgotadoTexto
import com.example.kaiju.ui.theme.DisponibleTexto
import com.example.kaiju.ui.theme.StockBajoTexto



private fun formatearFechaLocal(fechaIso: String): String {
    return runCatching {
        Instant.parse(fechaIso)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale("es", "CL")))
    }.getOrDefault(fechaIso)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    producto: Producto,
    movimientos: List<Movimiento>,
    fotoProducto: Bitmap?,
    mensajeServidor: String? = null,
    onMensajeMostrado: () -> Unit = {},
    onFotoTomada: (Bitmap) -> Unit,
    onRegistrarMovimiento: (tipo: String, cantidad: Int, motivo: String) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    // null = cuadro cerrado; "Entrada" o "Salida" = cuadro abierto
    var tipoDialogo by remember { mutableStateOf<String?>(null) }

    var movimientoPendiente by remember { mutableStateOf<String?>(null) }
    var mensajeVisible by remember { mutableStateOf<String?>(null) }
    var esConfirmacion by remember { mutableStateOf(false) }

    LaunchedEffect(mensajeServidor) {
        if (!mensajeServidor.isNullOrBlank()) {
            val confirmado = mensajeServidor == "Movimiento registrado"

            esConfirmacion = confirmado
            mensajeVisible = if (confirmado) {
                when (movimientoPendiente) {
                    "Entrada" -> "Entrada registrada correctamente"
                    "Salida" -> "Salida registrada correctamente"
                    else -> "Movimiento registrado correctamente"
                }
            } else {
                mensajeServidor
            }

            movimientoPendiente = null

            delay(3000)
            mensajeVisible = null
            onMensajeMostrado()
        }
    }

    val tomarFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { foto ->
        if (foto != null) {
            onFotoTomada(foto)
        }
    }

    val pedirPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permisoConcedido ->
        if (permisoConcedido) {
            tomarFoto.launch(null)
        }
    }

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

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Foto del producto",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (fotoProducto != null) {
                                Image(
                                    bitmap = fotoProducto.asImageBitmap(),
                                    contentDescription = "Foto del producto",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )

                                Text(
                                    text = "Foto capturada correctamente",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text("Todavía no hay una foto")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        tomarFoto.launch(null)
                                    } else {
                                        pedirPermisoCamara.launch(
                                            Manifest.permission.CAMERA
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Tomar foto")
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

                if (movimientos.isEmpty()) {
                    item {
                        Text("Aún no hay movimientos registrados")
                    }
                }

                items(movimientos) { m ->
                    // La salida resta; entrada y ajuste muestran la cantidad tal como se registró
                    val variacion = if (m.tipo == "Salida") -m.cantidad else m.cantidad

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${m.tipo} · ${formatearFechaLocal(m.fecha)}", fontWeight = FontWeight.Bold)
                                Text(m.motivo, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                text = if (variacion > 0) "+$variacion" else "$variacion",
                                fontWeight = FontWeight.Bold,
                                color = if (variacion < 0) AgotadoTexto else DisponibleTexto
                            )
                        }
                    }
                }
            }

            // Botones fijos abajo

            if (mensajeVisible != null) {
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
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorFondo
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (esConfirmacion) "✓" else "!",
                            fontWeight = FontWeight.Bold,
                            color = colorTexto
                        )

                        Text(
                            text = "  $mensajeVisible",
                            color = colorTexto,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { tipoDialogo = "Entrada" }, modifier = Modifier.weight(1f)) {
                    Text("Registrar entrada")
                }
                OutlinedButton(onClick = { tipoDialogo = "Salida" }, modifier = Modifier.weight(1f)) {
                    Text("Registrar salida")
                }
            }
        }
    }

    // Cuadro para pedir cantidad y motivo
    tipoDialogo?.let { tipo ->
        DialogoMovimiento(
            tipo = tipo,
            stockActual = producto.stock,
            onConfirmar = { cantidad, motivo ->
                movimientoPendiente = tipo
                onRegistrarMovimiento(tipo, cantidad, motivo)
                tipoDialogo = null
            },
            onCancelar = { tipoDialogo = null }
        )
    }
}

@Composable
fun DialogoMovimiento(
    tipo: String,
    stockActual: Int,
    onConfirmar: (Int, String) -> Unit,
    onCancelar: () -> Unit
) {
    var cantidadTexto by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    val cantidad = cantidadTexto.toIntOrNull()
    val error = when {
        cantidadTexto.isEmpty() -> null
        cantidad == null || cantidad <= 0 -> "Ingresa una cantidad mayor a 0"
        tipo == "Salida" && cantidad > stockActual -> "Solo hay $stockActual uds en stock"
        else -> null
    }
    val valido = cantidad != null && cantidad > 0 && error == null && motivo.isNotBlank()

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar ${tipo.lowercase()}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = { cantidadTexto = it.filter { c -> c.isDigit() } },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(cantidad!!, motivo.trim()) },
                enabled = valido
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}