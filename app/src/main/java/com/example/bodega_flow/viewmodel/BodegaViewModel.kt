package com.example.bodega_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.ExistenciaDto
import com.example.bodega_flow.repository.BodegaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BodegaUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val bodegas: List<BodegaDto> = emptyList(),
    val productos: List<ExistenciaDto> = emptyList()
)

class BodegaViewModel(
    private val repo: BodegaRepository = BodegaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodegaUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarBodegas() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }
                val bodegas = repo.getBodegas()
                _uiState.update { it.copy(loading = false, bodegas = bodegas) }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun cargarProductosDeBodega(bodegaId: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }

                val productos = repo.getProductosDeBodega(bodegaId)

                _uiState.update {
                    it.copy(
                        loading = false,
                        productos = productos
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}
