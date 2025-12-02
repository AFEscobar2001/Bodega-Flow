package com.example.bodega_flow.repository

import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.ExistenciaDto
import com.example.bodega_flow.data.parseHttpError
import com.example.bodega_flow.network.ApiClient
import retrofit2.HttpException

class BodegaRepository {

    private val api = ApiClient.api

    suspend fun getBodegas(): List<BodegaDto> {
        return try {
            api.getBodegas()
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }

    suspend fun getProductosDeBodega(bodegaId: Long): List<ExistenciaDto> {
        return try {
            api.getProductosDeBodega(bodegaId)
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }
}
