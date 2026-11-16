package com.example.kaiju.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto

@Database(
    entities = [Producto::class, Movimiento::class],
    version = 1,
    exportSchema = false
)
abstract class KaijuDatabase : RoomDatabase() {

    abstract fun inventarioDao(): InventarioDao

    companion object {
        // @Volatile: todos los hilos ven siempre el valor más reciente
        @Volatile
        private var instancia: KaijuDatabase? = null

        fun obtener(context: Context): KaijuDatabase {
            // Si ya existe, la devuelve; si no, la crea (una sola vez)
            return instancia ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    KaijuDatabase::class.java,
                    "kaiju.db"
                ).build().also { instancia = it }
            }
        }
    }
}