package com.example.kaiju.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigoProducto: String,
    val tipo: String,
    val cantidad: Int,
    val motivo: String,
    val fecha: String
)