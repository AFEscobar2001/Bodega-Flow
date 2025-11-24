package com.example.bodega_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.MovimientoCreateDto
import com.example.bodega_flow.data.MovimientoDto
import com.example.bodega_flow.data.MotivoMovimientoDto
import com.example.bodega_flow.repository.MovimientoRepository
import com.example.bodega_flow.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MovimientosUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val movimientos: List<MovimientoDto> = emptyList(),
    val motivos: List<MotivoMovimientoDto> = emptyList(),
    val bodegas: List<BodegaDto> = emptyList()
)

class MovimientoViewModel : ViewModel() {

    private val repo = MovimientoRepository()
    private val productoRepo = ProductoRepository() // solo si necesitas productos aquí

    private val _uiState = MutableStateFlow(MovimientosUiState())
    val uiState: StateFlow<MovimientosUiState> = _uiState

    fun cargarCatalogos() {
        viewModelScope.launch {
            try {
                val motivos = repo.getMotivos()
                // si tienes endpoint de bodegas en otro repo, cámbialo aquí
                val bodegas = emptyList<BodegaDto>()
                _uiState.value = _uiState.value.copy(
                    motivos = motivos,
                    bodegas = bodegas
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al cargar catálogos"
                )
            }
        }
    }

    fun cargarMovimientos(productoId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val lista = repo.getMovimientosPorProducto(productoId)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    movimientos = lista
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar movimientos"
                )
            }
        }
    }

    fun crearMovimiento(
        productoId: Long,
        bodegaId: Long,
        usuarioId: Long,
        motivoId: Long,
        tipo: String,
        cantidad: Double,
        comentario: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                repo.crearMovimiento(
                    MovimientoCreateDto(
                        productoId = productoId,
                        bodegaId = bodegaId,
                        usuarioId = usuarioId,
                        motivoId = motivoId,
                        tipo = tipo,
                        cantidad = cantidad,
                        comentario = comentario
                    )
                )
                cargarMovimientos(productoId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error al crear movimiento"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
