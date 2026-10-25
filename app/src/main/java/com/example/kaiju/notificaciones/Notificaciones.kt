package com.example.kaiju.notificaciones

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.kaiju.model.Producto

const val CANAL_STOCK = "stock_bajo"

//muestra una notificación cuando un producto llega a su stock mínimo
@SuppressLint("MissingPermission")
fun avisarStockBajo(context: Context, producto: Producto) {
    //
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    //el canal agrupa las notificaciones de un mismo tipo.
    val canal = NotificationChannel(CANAL_STOCK, "Stock bajo", NotificationManager.IMPORTANCE_DEFAULT)
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(canal)

    val texto = if (producto.stock == 0) {
        "Se agotó el producto ${producto.codigo}"
    } else {
        "Quedan ${producto.stock} unidades (mínimo ${producto.stockMinimo})"
    }

    val notificacion = NotificationCompat.Builder(context, CANAL_STOCK)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Stock bajo: ${producto.nombre}")
        .setContentText(texto)
        .setAutoCancel(true)
        .build()

    //el número identifica la notificación: un producto, una notificación
    NotificationManagerCompat.from(context).notify(producto.codigo.hashCode(), notificacion)
}