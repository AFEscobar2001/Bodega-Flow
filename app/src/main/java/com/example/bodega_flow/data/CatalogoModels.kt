package com.example.bodega_flow.data

import java.time.LocalDateTime

data class UnidadMedidaDto(
    val id: Long,
    val codigo: String,
    val descripcion: String
)

data class CategoriaDto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val activo: Boolean
)


data class MotivoMovimientoDto(
    val id: Long,
    val codigo: String,
    val tipo: String,       // IN, OUT, ADJUST
    val descripcion: String?
)