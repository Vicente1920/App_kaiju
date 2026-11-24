package com.example.kaiju.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.theme.AgotadoTexto
import com.example.kaiju.validacion.stockResultante
import com.example.kaiju.validacion.validarCantidad
import com.example.kaiju.validacion.validarMotivo
import com.example.kaiju.validacion.validarProducto
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(
    productos: List<Producto>,
    tipoInicial: String = "Entrada",
    mensajeServidor: String? = null,
    onMensajeMostrado: () -> Unit = {},
    onConfirmar: (codigo: String, tipo: String, cantidad: Int, motivo: String) -> Unit
) {
    var tipo by rememberSaveable(tipoInicial) { mutableStateOf(tipoInicial) }
    var codigoElegido by rememberSaveable { mutableStateOf<String?>(null) }
    var menuAbierto by rememberSaveable { mutableStateOf(false) }
    var cantidad by rememberSaveable { mutableIntStateOf(1) }
    var motivo by rememberSaveable { mutableStateOf("") }
    var mensajeVisible by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf(false) }
    var tipoPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    var errorProducto by rememberSaveable { mutableStateOf<String?>(null) }
    var errorCantidad by rememberSaveable { mutableStateOf<String?>(null) }
    var errorMotivo by rememberSaveable { mutableStateOf<String?>(null) }

    val producto = productos.find { it.codigo == codigoElegido }

    LaunchedEffect(mensajeServidor) {
        if (!mensajeServidor.isNullOrBlank()) {
            confirmacion = mensajeServidor == "Movimiento registrado"

            mensajeVisible = if (confirmacion) {
                when (tipoPendiente) {
                    "Entrada" -> "Entrada registrada correctamente"
                    "Salida" -> "Salida registrada correctamente"
                    "Ajuste" -> "Ajuste registrado correctamente"
                    else -> "Movimiento registrado correctamente"
                }
            } else {
                mensajeServidor
            }

            if (confirmacion) {
                cantidad = 1
                motivo = ""
            }

            tipoPendiente = null

            delay(3000)
            mensajeVisible = ""
            onMensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar movimiento") },
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Entrada", "Salida", "Ajuste").forEach { opcion ->
                    FilterChip(
                        selected = tipo == opcion,
                        onClick = {
                            tipo = opcion

                            if (opcion != "Ajuste" && cantidad < 1) {
                                cantidad = 1
                            }

                            if (
                                opcion == "Salida" &&
                                producto != null &&
                                cantidad > producto.stock
                            ) {
                                cantidad = maxOf(producto.stock, 1)
                            }
                        },
                        label = { Text(opcion) }
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (producto != null) {
                            Text(producto.nombre)
                            Text("${producto.codigo} stock actual ${producto.stock}")
                        } else {
                            Text("Ningún producto seleccionado")
                        }
                    }

                    Box {
                        Button(onClick = { menuAbierto = true }) {
                            Text("Elegir")
                        }

                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            productos.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.codigo} ${p.nombre}") },
                                    onClick = {
                                        codigoElegido = p.codigo
                                        menuAbierto = false

                                        if (tipo == "Salida" && cantidad > p.stock) {
                                            cantidad = maxOf(p.stock, 1)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            MensajeError(errorProducto)

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (tipo == "Ajuste" || cantidad > 1) {
                                cantidad--
                            }
                        }
                    ) {
                        Text("-")
                    }

                    Text(
                        text = "$cantidad",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Button(
                        onClick = {
                            val maximo =
                                if (tipo == "Salida" && producto != null) {
                                    producto.stock
                                } else {
                                    Int.MAX_VALUE
                                }

                            if (cantidad < maximo) {
                                cantidad++
                            }
                        }
                    ) {
                        Text("+")
                    }
                }
            }

            MensajeError(errorCantidad)

            OutlinedTextField(
                value = motivo,
                onValueChange = {
                    motivo = it
                    errorMotivo = null
                },
                label = { Text("Motivo") },
                singleLine = true,
                isError = errorMotivo != null,
                modifier = Modifier.fillMaxWidth()
            )

            MensajeError(errorMotivo)

            if (producto != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Stock resultante")
                        Text("${stockResultante(producto, tipo, cantidad)} uds")
                    }
                }
            }

            Button(
                onClick = {
                    errorProducto = validarProducto(producto)
                    errorCantidad = validarCantidad(producto, tipo, cantidad)
                    errorMotivo = validarMotivo(motivo)

                    if (
                        errorProducto == null &&
                        errorCantidad == null &&
                        errorMotivo == null &&
                        producto != null
                    ) {
                        tipoPendiente = tipo
                        onConfirmar(producto.codigo, tipo, cantidad, motivo)
                    } else {
                        mensajeVisible = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Text(
                    text = "Confirmar movimiento",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(
                visible = mensajeVisible.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val colorFondo = if (confirmacion) {
                    Color(0xFFE8F5E9)
                } else {
                    Color(0xFFFFEBEE)
                }

                val colorTexto = if (confirmacion) {
                    Color(0xFF1B5E20)
                } else {
                    MaterialTheme.colorScheme.error
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorFondo
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (confirmacion) {
                                Icons.Default.Check
                            } else {
                                Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = colorTexto,
                            modifier = Modifier.size(22.dp)
                        )

                        Text(
                            text = mensajeVisible,
                            color = colorTexto,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MensajeError(mensaje: String?) {
    if (mensaje != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = AgotadoTexto,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = mensaje,
                color = AgotadoTexto,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}