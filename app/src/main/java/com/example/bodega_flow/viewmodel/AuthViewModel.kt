package com.example.bodega_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega_flow.data.AuthResponse
import com.example.bodega_flow.data.LoginRequest
import com.example.bodega_flow.data.RegisterRequest
import com.example.bodega_flow.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(
        email: String,
        password: String,
        onSuccess: (AuthResponse) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }
                val resp = repo.login(LoginRequest(email = email, password = password))
                _uiState.update { it.copy(loading = false) }
                onSuccess(resp)
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun register(
        nombre: String,
        email: String,
        username: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }
                repo.register(
                    RegisterRequest(
                        nombre = nombre,
                        email = email,
                        username = username,
                        password = password
                    )
                )
                _uiState.update { it.copy(loading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
