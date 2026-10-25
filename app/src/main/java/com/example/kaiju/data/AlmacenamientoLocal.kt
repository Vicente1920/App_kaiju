package com.example.kaiju.data

import android.content.Context
import com.example.kaiju.model.Producto
import org.json.JSONArray
import org.json.JSONObject
import com.example.kaiju.data.productosDeEjemplo


private const val ARCHIVO = "inventario_local"
private const val CLAVE_PRODUCTOS = "productos"

fun cargarProductos(context: Context): List<Producto> {
    val texto = context
        .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        .getString(CLAVE_PRODUCTOS, null)
        ?: return productosDeEjemplo()

    return try {
        val lista = JSONArray(texto)

        List(lista.length()) { indice ->
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
    } catch (_: Exception) {
        productosDeEjemplo()
    }
}
