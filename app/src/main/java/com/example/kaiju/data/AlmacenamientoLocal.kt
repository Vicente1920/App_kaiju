package com.example.kaiju.data

import android.content.Context
import android.util.Log
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import org.json.JSONArray
import org.json.JSONObject

private const val ARCHIVO = "inventario_local"
private const val CLAVE_PRODUCTOS = "productos"
private const val CLAVE_MOVIMIENTOS = "movimientos"

fun cargarProductos(context: Context): List<Producto> {
    val texto = context
        .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        .getString(CLAVE_PRODUCTOS, null)

    if (texto == null) {
        Log.d("KAIJU", "No hay nada guardado, uso datos de ejemplo")
        return productosDeEjemplo()
    }

    return try {
        val lista = JSONArray(texto)

        val productos = List(lista.length()) { indice ->
            val item = lista.getJSONObject(indice)

            Producto(
                codigo = item.getString("codigo"),
                nombre = item.getString("nombre"),
                categoria = item.getString("categoria"),
                precio = item.getDouble("precio"),
                stock = item.getInt("stock"),
                stockMinimo = item.getInt("stockMinimo"),
                talla = item.optString("talla")
                    .takeIf { it.isNotEmpty() },
                color = item.optString("color")
                    .takeIf { it.isNotEmpty() },
                modelo = item.optString("modelo")
                    .takeIf { it.isNotEmpty() }
            )
        }
        Log.d("KAIJU", "Cargados ${productos.size} productos guardados")
        productos
    } catch (e: Exception) {
        Log.e("KAIJU", "Error al leer lo guardado", e)
        productosDeEjemplo()
    }
}

fun guardarProductos(context: Context, productos: List<Producto>) {
    val lista = JSONArray()

    productos.forEach { p ->
        val item = JSONObject()
        item.put("codigo", p.codigo)
        item.put("nombre", p.nombre)
        item.put("categoria", p.categoria)
        item.put("precio", p.precio)
        item.put("stock", p.stock)
        item.put("stockMinimo", p.stockMinimo)
        item.put("talla", p.talla ?: "")
        item.put("color", p.color ?: "")
        item.put("modelo", p.modelo ?: "")
        lista.put(item)
    }

    context
        .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        .edit()
        .putString(CLAVE_PRODUCTOS, lista.toString())
        .apply()

    Log.d("KAIJU", "Guardados ${productos.size} productos")
}

fun cargarMovimientos(context: Context): List<Movimiento> {
    val texto = context
        .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        .getString(CLAVE_MOVIMIENTOS, null)
        ?: return emptyList()

    return try {
        val lista = JSONArray(texto)

        List(lista.length()) { indice ->
            val item = lista.getJSONObject(indice)

            Movimiento(
                codigoProducto = item.getString("codigoProducto"),
                tipo = item.getString("tipo"),
                cantidad = item.getInt("cantidad"),
                motivo = item.getString("motivo"),
                fecha = item.getString("fecha")
            )
        }
    } catch (e: Exception) {
        Log.e("KAIJU", "Error al leer movimientos", e)
        emptyList()
    }
}

fun guardarMovimientos(context: Context, movimientos: List<Movimiento>) {
    val lista = JSONArray()

    movimientos.forEach { m ->
        val item = JSONObject()
        item.put("codigoProducto", m.codigoProducto)
        item.put("tipo", m.tipo)
        item.put("cantidad", m.cantidad)
        item.put("motivo", m.motivo)
        item.put("fecha", m.fecha)
        lista.put(item)
    }

    context
        .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        .edit()
        .putString(CLAVE_MOVIMIENTOS, lista.toString())
        .apply()

    Log.d("KAIJU", "Guardados ${movimientos.size} movimientos")
}