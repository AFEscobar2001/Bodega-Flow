package com.example.bodega_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.MovimientoCreateDto
import com.example.bodega_flow.data.MovimientoDto
import com.example.bodega_flow.repository.BodegaRepository
import com.example.bodega_flow.repository.MovimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovimientoUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val movimientos: List<MovimientoDto> = emptyList(),
    val bodegas: List<BodegaDto> = emptyList()
)

class MovimientoViewModel(
    private val repo: MovimientoRepository = MovimientoRepository(),
    private val bodegaRepo: BodegaRepository = BodegaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovimientoUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarCatalogos() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }
                val bodegas = bodegaRepo.getBodegas()
                _uiState.update { it.copy(loading = false, bodegas = bodegas) }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun cargarMovimientos(productoId: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }
                val movimientos = repo.getMovimientosPorProducto(productoId)
                _uiState.update { it.copy(loading = false, movimientos = movimientos) }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun crearMovimiento(
        productoId: Long,
        bodegaOrigenId: Long,
        bodegaDestinoId: Long?,
        usuarioId: Long,
        tipo: String,
        cantidad: Double,
        comentario: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }

                val dto = MovimientoCreateDto(
                    productoId = productoId,
                    bodegaOrigenId = bodegaOrigenId,
                    bodegaDestinoId = bodegaDestinoId,
                    usuarioId = usuarioId,
                    tipo = tipo,
                    cantidad = cantidad,
                    comentario = comentario
                )

                val creado = repo.crearMovimiento(dto)

                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        movimientos = state.movimientos + creado
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}
