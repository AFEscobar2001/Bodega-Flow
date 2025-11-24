package com.example.bodega_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega_flow.data.AuthResponse
import com.example.bodega_flow.data.LoginRequest
import com.example.bodega_flow.data.RegisterRequest
import com.example.bodega_flow.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val user: AuthResponse? = null,
    val registerSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Correo y contraseña son obligatorios"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val auth = repo.login(LoginRequest(email = email, password = password))
                _uiState.value = AuthUiState(
                    loading = false,
                    error = null,
                    user = auth,
                    registerSuccess = false
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    loading = false,
                    error = e.message ?: "Error al iniciar sesión",
                    user = null,
                    registerSuccess = false
                )
            }
        }
    }

    fun register(nombre: String, email: String, username: String, password: String) {
        if (nombre.isBlank() || email.isBlank() || username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Nombre, correo, usuario y contraseña son obligatorios"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, registerSuccess = false)
            try {
                repo.register(
                    RegisterRequest(
                        nombre = nombre,
                        email = email,
                        username = username,
                        password = password
                    )
                )
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = null,
                    registerSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error al registrar usuario",
                    registerSuccess = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearUser() {
        _uiState.value = _uiState.value.copy(user = null)
    }

    fun clearRegisterSuccess() {
        _uiState.value = _uiState.value.copy(registerSuccess = false)
    }
}
