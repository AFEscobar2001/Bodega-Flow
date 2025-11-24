package com.example.bodega_flow.repository

import com.example.bodega_flow.data.AuthResponse
import com.example.bodega_flow.data.LoginRequest
import com.example.bodega_flow.data.RegisterRequest
import com.example.bodega_flow.data.UsuarioDto
import com.example.bodega_flow.network.ApiClient
import com.example.bodega_flow.data.parseHttpError
import retrofit2.HttpException

class AuthRepository {

    private val api = ApiClient.api

    suspend fun register(req: RegisterRequest): UsuarioDto {
        return try {
            api.register(req)
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }

    suspend fun login(req: LoginRequest): AuthResponse {
        return try {
            api.login(req)
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }
}
