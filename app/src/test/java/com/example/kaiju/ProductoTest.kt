package com.example.kaiju

import com.example.kaiju.data.productosDeEjemplo
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductosTest {

    @Test
    fun lista_de_ejemplo_tiene_5_productos() {
        productosDeEjemplo().forEach { println("${it.codigo} - ${it.nombre}: ${it.stock}") }
        assertEquals(5, productosDeEjemplo().size)
    }
}