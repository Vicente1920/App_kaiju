package com.example.kaiju.validacion

import com.example.kaiju.model.Producto


// cuanto quedaria el stock si se confirma el movimiento
fun stockResultante(producto: Producto, tipo: String, cantidad: Int): Int{
    if (tipo == "Salida"){
        return producto.stock - cantidad
    }
    return producto.stock + cantidad
}

// cada funcion devuelve mensaje de error o null si el campo ta bien

fun validarProducto(producto: Producto?): String? {
    if (producto == null){
        return "Selecciona un producto"
    }
    return null
}

fun validarCantidad(producto: Producto?, tipo: String, cantidad: Int): String? {
    if (cantidad == 0) {
        return "La cantidad no puede ser 0"
    }
    if (tipo != "Ajuste" && cantidad < 0){
        return "La cantidad debe ser mayor a 0"
    }
    if (producto != null && stockResultante(producto, tipo, cantidad) < 0){
        return "No hay stock suficiente (disponible: ${producto.stock})"
    }
    return null
}

fun validarMotivo(motivo: String): String? {
    if (motivo.isBlank()){
        return "Ingresa un motivo"
    }
    return null
}