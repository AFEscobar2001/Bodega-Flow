package com.example.bodega_flow.data

import java.time.LocalDateTime

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val username: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val id: Long,
    val nombre: String,
    val username: String
)

data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val email: String,
    val username: String,
    val activo: Boolean
)

data class UsuarioUpdateRequest(
    val nombre: String,
    val email: String?,
    val username: String
)