package com.example.kaiju.model

data class Producto(
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int,
    val talla: String? = null,
    val color: String? = null,
    val modelo: String? = null
) {
    val stockBajo: Boolean get() = stock <= stockMinimo
}