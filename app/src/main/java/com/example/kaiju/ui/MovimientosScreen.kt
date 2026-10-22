package com.example.kaiju.ui

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.theme.AgotadoTexto
import com.example.kaiju.validacion.stockResultante
import com.example.kaiju.validacion.validarCantidad
import com.example.kaiju.validacion.validarMotivo
import com.example.kaiju.validacion.validarProducto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(
    productos: List<Producto>,
    onConfirmar: (codigo: String, tipo: String, cantidad: Int, motivo: String) -> Unit
){
    var tipo by remember {mutableStateOf("Entrada")}
    var codigoElegido by remember { mutableStateOf<String?>(null)}
    var menuAbierto by remember { mutableStateOf(false)}
    var cantidad by remember { mutableIntStateOf(1)}
    var motivo by remember { mutableStateOf("")}
    var mensajeExito by remember { mutableStateOf("")}

    // mensaje de error por campo
    var errorProducto by remember {mutableStateOf<String?>(null)}
    var errorCantidad by remember { mutableStateOf<String?>(null)}
    var errorMotivo by remember { mutableStateOf<String?>(null)}


    //producto elegido se busca por codigo
    val producto = productos.find { it.codigo == codigoElegido}

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
            //tipo de movimiento
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                listOf("Entrada", "Salida", "Ajuste").forEach { opcion ->
                    FilterChip(
                        selected = tipo == opcion,
                        onClick = {tipo = opcion},
                        label = { Text(opcion)}
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
                            Text("Ningun producto seleccionado")
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
                                    text = { Text("${p.codigo} ${p.nombre}")},
                                    onClick = {
                                        codigoElegido = p.codigo
                                        menuAbierto = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            MensajeError(errorProducto)

            //cantidad botones
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    OutlinedButton(onClick = { cantidad-- }) { Text("-") }
                    Text(
                        text = "$cantidad",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Button(onClick = { cantidad++ }) { Text("+") }
                }
            }
            MensajeError(errorCantidad)

            //motivo : el campo se pinta rojo si hay error
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo") },
                singleLine = true,
                isError = errorMotivo != null,
                modifier = Modifier.fillMaxWidth()
            )
            MensajeError(errorMotivo)

            //stock resultante: se recalcula solo cuando algo cambia del formulario
            if (producto != null) {
                Card(modifier = Modifier.fillMaxWidth()){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text("Stock resultante")
                        Text(
                            text = "${stockResultante(producto, tipo, cantidad)} uds"
                        )
                    }
                }
            }
            //confirmar: valida cada campo y avisa si no hay error

            Button(
                onClick = {
                    errorProducto = validarProducto(producto)
                    errorCantidad = validarCantidad(producto, tipo, cantidad)
                    errorMotivo = validarMotivo(motivo)

                    if (errorProducto == null && errorCantidad == null && errorMotivo == null && producto != null){
                        onConfirmar(producto.codigo, tipo, cantidad, motivo)
                        mensajeExito = "Movimiento registrado"
                        cantidad = 1
                        motivo = ""
                    } else {
                        mensajeExito = ""
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(Icons.Default.Check, contentDescription = null)
                Text("Confirmar movimiento", modifier = Modifier.padding(start = 8.dp))
            }

            if (mensajeExito.isNotEmpty()){
                Text(mensajeExito, color = MaterialTheme.colorScheme.primary)
            }
        }
    }

}

//mensaje de error con icono, no muestra na si no hay error


@Composable
fun MensajeError(mensaje: String?){
    if(mensaje != null) {
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