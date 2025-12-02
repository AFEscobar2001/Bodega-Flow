package com.example.bodega_flow.data

data class ProductoCreateDto(
    val codigo: String,
    val nombre: String,
    val categoriaId: Long,
    val unidadMedidaId: Long,
    val cantidadInicial: Double,
    val bodegaId: Long? = null
)

data class ProductoDto(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val categoriaId: Long,
    val categoriaNombre: String?,
    val unidadMedidaId: Long,
    val unidadMedidaCodigo: String?,
    val activo: Boolean,
    val createdAt: String?
)


