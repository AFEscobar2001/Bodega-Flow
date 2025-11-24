package com.example.bodega_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega_flow.data.CategoriaDto
import com.example.bodega_flow.data.ProductoCreateDto
import com.example.bodega_flow.data.ProductoDto
import com.example.bodega_flow.data.UnidadMedidaDto
import com.example.bodega_flow.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductosUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val productos: List<ProductoDto> = emptyList(),
    val categorias: List<CategoriaDto> = emptyList(),
    val unidades: List<UnidadMedidaDto> = emptyList()
)

class ProductoViewModel : ViewModel() {

    private val repo = ProductoRepository()

    private val _uiState = MutableStateFlow(ProductosUiState())
    val uiState: StateFlow<ProductosUiState> = _uiState

    fun cargarCatalogosYProductos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val categorias = repo.getCategorias()
                val unidades = repo.getUnidadesMedida()
                val productos = repo.getProductos()
                _uiState.value = ProductosUiState(
                    loading = false,
                    error = null,
                    productos = productos,
                    categorias = categorias,
                    unidades = unidades
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar productos"
                )
            }
        }
    }

    fun crearProducto(
        codigo: String,
        nombre: String,
        categoriaId: Long,
        unidadId: Long
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                repo.crearProducto(
                    ProductoCreateDto(
                        codigo = codigo,
                        nombre = nombre,
                        categoriaId = categoriaId,
                        unidadMedidaId = unidadId
                    )
                )
                // recarga lista
                cargarCatalogosYProductos()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error al crear producto"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
