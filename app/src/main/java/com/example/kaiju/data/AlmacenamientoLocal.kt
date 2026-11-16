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
