package com.example.bodega_flow.data

import java.time.LocalDateTime

data class MovimientoCreateDto(
    val productoId: Long,
    val bodegaId: Long,
    val usuarioId: Long,
    val motivoId: Long,
    val tipo: String,        // IN, OUT, ADJUST
    val cantidad: Double,
    val comentario: String?
)

data class MovimientoDto(
    val id: Long,
    val productoId: Long,
    val bodegaId: Long,
    val usuarioId: Long,
    val motivoId: Long,
    val tipo: String,
    val cantidad: Double,
    val comentario: String?,
    val createdAt: String,   // LocalDateTime serializado
    val refMovimientoId: Long?
)