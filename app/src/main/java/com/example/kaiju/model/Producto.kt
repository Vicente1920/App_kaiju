package com.example.kaiju.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey
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