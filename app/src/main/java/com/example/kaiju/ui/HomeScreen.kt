package com.example.kaiju.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.kaiju.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    /* aqui va la variable antes de scaffold*/
    var contador by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi App Kotlin") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                /*Container color es el fondo de la barra y titlecontentcolor es el color del titulo
                * OnPrimary es el color pensado para leerse encima de primary por eso se usan juntos */
                ))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp), /*pone 20dp de separacion entre cada hijo del COLUMN, sin esto tendriamos que usar padding a cada elemento x separado */
            horizontalAlignment = Alignment.CenterHorizontally
            /*En el column, el verticalArragment reparte los elementos hacia abajo y el horizontal los posiciona de ancho */
        ) {
            Text(text = "¡Bienvenido!",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium)

            Button(onClick = { contador++ }) {
                Text("Presióname")
            }

            Text(text = "Veces presionado: $contador")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                /* suma 5 al contador*/
                Button(
                    onClick = {contador+=5},
                    modifier = Modifier.weight(1f)
                ){
                    Text("sumar 5")
                }
                OutlinedButton( /* esto para reiniciar el contador a 0*/
                    onClick = { contador = 0},
                    modifier = Modifier.weight(1f)
                ){
                    Text("Reiniciar")
                }
            }
            /* logo de la app ajustado al ancho con altura fija*/
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
