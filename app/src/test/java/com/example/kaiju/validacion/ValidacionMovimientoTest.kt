package com.example.kaiju.validacion

import com.example.kaiju.model.Producto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionMovimientoTest {

    private val producto = Producto(
        codigo = "KJ-001",
        nombre = "Polera Kaiju Negra M",
        categoria = "Ropa",
        precio = 12990.0,
        stock = 24,
        stockMinimo = 5
    )

    @Test
    fun entrada_aumenta_el_stock() {
        val resultado = stockResultante(
            producto = producto,
            tipo = "Entrada",
            cantidad = 5
        )

        assertEquals(29, resultado)
    }

    @Test
    fun salida_disminuye_el_stock() {
        val resultado = stockResultante(
            producto = producto,
            tipo = "Salida",
            cantidad = 20
        )

        assertEquals(4, resultado)
    }

    @Test
    fun producto_nulo_devuelve_error() {
        val resultado = validarProducto(null)

        assertEquals(
            "Selecciona un producto",
            resultado
        )
    }

    @Test
    fun producto_seleccionado_no_devuelve_error() {
        val resultado = validarProducto(producto)

        assertNull(resultado)
    }

    @Test
    fun cantidad_cero_devuelve_error() {
        val resultado = validarCantidad(
            producto = producto,
            tipo = "Salida",
            cantidad = 0
        )

        assertEquals(
            "La cantidad no puede ser 0",
            resultado
        )
    }

    @Test
    fun salida_mayor_al_stock_devuelve_error() {
        val resultado = validarCantidad(
            producto = producto,
            tipo = "Salida",
            cantidad = 25
        )

        assertEquals(
            "No hay stock suficiente (disponible: 24)",
            resultado
        )
    }

    @Test
    fun salida_valida_no_devuelve_error() {
        val resultado = validarCantidad(
            producto = producto,
            tipo = "Salida",
            cantidad = 20
        )

        assertNull(resultado)
    }

    @Test
    fun motivo_vacio_devuelve_error() {
        val resultado = validarMotivo("")

        assertEquals(
            "Ingresa un motivo",
            resultado
        )
    }

    @Test
    fun motivo_con_texto_no_devuelve_error() {
        val resultado = validarMotivo("Venta de demostración")

        assertNull(resultado)
    }
}