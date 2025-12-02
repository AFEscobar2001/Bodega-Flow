package com.example.bodega_flow.data

data class MovimientoCreateDto(
    val productoId: Long,
    val bodegaOrigenId: Long,
    val bodegaDestinoId: Long?,
    val usuarioId: Long,
    val tipo: String,
    val cantidad: Double,
    val comentario: String?
)

data class MovimientoDto(
    val id: Long,
    val productoId: Long,
    val productoNombre: String,
    val bodegaId: Long,
    val bodegaNombre: String,
    val usuarioId: Long,
    val usuarioNombre: String,
    val tipo: String,
    val cantidad: Double,
    val comentario: String?,
    val createdAt: String,
    val refMovimientoId: Long?
)

data class MovimientoUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val movimientos: List<MovimientoDto> = emptyList(),
    val bodegas: List<BodegaDto> = emptyList()
)