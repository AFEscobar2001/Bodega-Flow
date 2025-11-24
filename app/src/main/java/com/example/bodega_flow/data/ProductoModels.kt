package com.example.bodega_flow.data

import java.time.LocalDateTime

data class ProductoCreateDto(
    val codigo: String,
    val nombre: String,
    val categoriaId: Long,
    val unidadMedidaId: Long
)

data class ProductoDto(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val categoriaId: Long,
    val unidadMedidaId: Long,
    val activo: Boolean
)

data class ExistenciaDto(
    val id: Long,
    val productoId: Long,
    val bodegaId: Long,
    val cantidad: Double
)