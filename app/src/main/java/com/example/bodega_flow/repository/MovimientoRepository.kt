package com.example.bodega_flow.repository

import com.example.bodega_flow.data.MotivoMovimientoDto
import com.example.bodega_flow.data.MovimientoCreateDto
import com.example.bodega_flow.data.MovimientoDto
import com.example.bodega_flow.network.ApiClient
import com.example.bodega_flow.data.parseHttpError
import retrofit2.HttpException

class MovimientoRepository {

    private val api = ApiClient.api

    suspend fun crearMovimiento(body: MovimientoCreateDto): MovimientoDto {
        return try {
            api.crearMovimiento(body)
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }

    suspend fun deshacerMovimiento(id: Long, usuarioId: Long): MovimientoDto {
        return try {
            api.deshacerMovimiento(id, usuarioId)
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }

    suspend fun getMovimientosPorProducto(productoId: Long): List<MovimientoDto> {
        return try {
            api.getMovimientosPorProducto(productoId)
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }

    suspend fun getMotivos(): List<MotivoMovimientoDto> {
        return try {
            api.getMotivosMovimiento()
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }
}
