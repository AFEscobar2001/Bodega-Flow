package com.example.bodega_flow.data

data class ExistenciaDto(
    val productoId: Long,
    val productoNombre: String,
    val bodegaId: Long,
    val bodegaNombre: String,
    val cantidad: Double,
    val updatedAt: String
)
