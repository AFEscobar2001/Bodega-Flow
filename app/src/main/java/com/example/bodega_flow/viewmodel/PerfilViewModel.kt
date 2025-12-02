package com.example.bodega_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega_flow.data.UsuarioDto
import com.example.bodega_flow.data.UsuarioUpdateRequest
import com.example.bodega_flow.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val usuario: UsuarioDto? = null
)

class PerfilViewModel(
    private val repo: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarUsuario(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }
                val user = repo.getUsuario(id)
                _uiState.update { it.copy(loading = false, usuario = user) }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun actualizarUsuario(id: Long, req: UsuarioUpdateRequest) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }
                val user = repo.actualizarUsuario(id, req)
                _uiState.update { it.copy(loading = false, usuario = user) }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
