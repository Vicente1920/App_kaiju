package com.example.kaiju.data

data class ProductoVendido(
    val nombre: String,
    val unidades: Int
)

fun diasSemana(): List<String> = listOf("L", "M", "X", "J", "V", "S", "D")


fun ventasPorDia(): List<Int> = listOf(140000, 190000, 110000, 240000, 330000, 200000, 74300)

fun masVendidos(): List<ProductoVendido> = listOf(
    ProductoVendido("Polera Kaiju Negra M", 42),
    ProductoVendido("Gorro Lana Gris", 27)
)

fun menosVendidos(): List<ProductoVendido> = listOf(
    ProductoVendido("Mochila Táctica", 1),
    ProductoVendido("Botella Térmica 750 ml", 3)
)