package com.example.bodega_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.ProductoCreateDto
import com.example.bodega_flow.data.ProductoDto
import com.example.bodega_flow.data.CategoriaDto
import com.example.bodega_flow.data.UnidadMedidaDto
import com.example.bodega_flow.repository.ProductoRepository
import com.example.bodega_flow.repository.BodegaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductoUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val productos: List<ProductoDto> = emptyList(),
    val categorias: List<CategoriaDto> = emptyList(),
    val unidades: List<UnidadMedidaDto> = emptyList(),
    val bodegas: List<BodegaDto> = emptyList()
)

class ProductoViewModel(
    private val repo: ProductoRepository = ProductoRepository(),
    private val bodegaRepo: BodegaRepository = BodegaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarCatalogosYProductos() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }

                val productos = repo.getProductos()
                val categorias = repo.getCategorias()
                val unidades = repo.getUnidadesMedida()
                val bodegas = bodegaRepo.getBodegas()

                _uiState.update {
                    it.copy(
                        loading = false,
                        productos = productos,
                        categorias = categorias,
                        unidades = unidades,
                        bodegas = bodegas
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun crearProducto(
        codigo: String,
        nombre: String,
        categoriaId: Long,
        unidadId: Long,
        cantidadInicial: Double,
        bodegaId: Long
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }

                val dto = ProductoCreateDto(
                    codigo = codigo,
                    nombre = nombre,
                    categoriaId = categoriaId,
                    unidadMedidaId = unidadId,
                    cantidadInicial = cantidadInicial,
                    bodegaId = bodegaId
                )

                val creado = repo.crearProducto(dto)

                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        productos = state.productos + creado
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
