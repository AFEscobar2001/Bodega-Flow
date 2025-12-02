package com.example.bodega_flow.repository

import com.example.bodega_flow.data.UsuarioDto
import com.example.bodega_flow.data.UsuarioUpdateRequest
import com.example.bodega_flow.data.parseHttpError
import com.example.bodega_flow.network.ApiClient
import retrofit2.HttpException

class UsuarioRepository {

    private val api = ApiClient.api

    suspend fun getUsuario(id: Long): UsuarioDto = try {
        api.getUsuario(id)
    } catch (e: HttpException) {
        throw Exception(parseHttpError(e))
    } catch (e: Exception) {
        throw Exception("Error al conectar con el servidor")
    }

    suspend fun actualizarUsuario(id: Long, body: UsuarioUpdateRequest): UsuarioDto = try {
        api.actualizarUsuario(id, body)
    } catch (e: HttpException) {
        throw Exception(parseHttpError(e))
    } catch (e: Exception) {
        throw Exception("Error al conectar con el servidor")
    }
}
