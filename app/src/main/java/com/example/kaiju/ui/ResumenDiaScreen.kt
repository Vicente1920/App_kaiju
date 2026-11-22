package com.example.kaiju.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kaiju.model.Movimiento
import com.example.kaiju.viewmodel.ResumenDiaViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenDiaScreen(
    movimientos: List<Movimiento>,
    resumenViewModel: ResumenDiaViewModel = viewModel()
) {
    val estado by resumenViewModel.estado.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(movimientos) {
        resumenViewModel.actualizar(movimientos)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Resumen del día")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Movimientos registrados hoy",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Entradas")
                        Text(
                            text = estado.entradas.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Salidas")
                        Text(
                            text = estado.salidas.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            Button(
                onClick = {
                    resumenViewModel.actualizar(movimientos)
                },
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar")
            }

            when {
                estado.cargando -> {
                    CircularProgressIndicator()

                    Text("Generando gráfico...")
                }

                estado.error != null -> {
                    val mensajeError = estado.error ?: "Error desconocido"

                    Text(
                        text = mensajeError,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                estado.imagenGrafico != null -> {
                    Text(
                        text = "Gráfico de movimientos de hoy",
                        style = MaterialTheme.typography.titleMedium
                    )

                    AsyncImage(
                        model = estado.imagenGrafico,
                        contentDescription = "Gráfico de entradas y salidas del día",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    Text("Presiona Actualizar para generar el gráfico")
                }
            }
        }
    }
}