package com.example.kaiju.resumen

import com.example.kaiju.model.Movimiento
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class ConteoMovimientos(
    val entradas: Int,
    val salidas: Int
)

fun contarMovimientosDeHoy(
    movimientos: List<Movimiento>,
    zona: ZoneId = ZoneId.systemDefault()
): ConteoMovimientos {
    val hoy = LocalDate.now(zona)

    val movimientosDeHoy = movimientos.filter { movimiento ->
        fechaDelMovimiento(movimiento.fecha, zona) == hoy
    }

    return ConteoMovimientos(
        entradas = movimientosDeHoy.count { it.tipo.equals("Entrada", ignoreCase = true) },
        salidas = movimientosDeHoy.count { it.tipo.equals("Salida", ignoreCase = true) }
    )
}

private fun fechaDelMovimiento(
    fecha: String,
    zona: ZoneId
): LocalDate? {
    return try {
        // Formato habitual enviado por PostgreSQL:
        // 2026-11-22T18:35:10.000Z
        Instant.parse(fecha)
            .atZone(zona)
            .toLocalDate()
    } catch (_: Exception) {
        try {
            // Otro formato posible:
            // 2026-11-22T18:35:10-03:00
            OffsetDateTime.parse(fecha)
                .atZoneSameInstant(zona)
                .toLocalDate()
        } catch (_: Exception) {
            try {
                // Formato sin zona horaria:
                // 2026-11-22T18:35:10
                LocalDateTime.parse(fecha)
                    .toLocalDate()
            } catch (_: Exception) {
                try {
                    // Si solamente viene la fecha:
                    // 2026-11-22
                    LocalDate.parse(fecha)
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
}