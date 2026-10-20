package com.example.kaiju.data

import com.example.kaiju.model.Producto

fun productosDeEjemplo(): List<Producto> = listOf(
    Producto("KJ-001", "Polera Kaiju Negra M", "Ropa", 12990.0, 24, 5, talla = "M", color = "Negro"),
    Producto("KJ-014", "Zapatilla Urbana 42", "Calzado", 39990.0, 3, 5, talla = "42", color = "Negro", modelo = "Runner"),
    Producto("KJ-022", "Mochila Táctica", "Accesorios", 29990.0, 0, 4),
    Producto("KJ-031", "Gorro Lana Gris", "Ropa", 8990.0, 18, 5),
    Producto("KJ-040", "Botella Térmica 750 ml", "Hogar", 14990.0, 6, 8)
    )

fun main() {
    productosDeEjemplo().forEach { println("${it.codigo} - ${it.nombre}: ${it.stock}") }
}