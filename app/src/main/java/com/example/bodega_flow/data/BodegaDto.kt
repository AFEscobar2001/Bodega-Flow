package com.example.bodega_flow.data

data class BodegaDto(
    val id: Long,
    val nombre: String,
    val ubicacion: String?,
    val activo: Boolean
)
