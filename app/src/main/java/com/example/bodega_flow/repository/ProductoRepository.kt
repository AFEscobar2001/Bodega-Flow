package com.example.bodega_flow.repository

import com.example.bodega_flow.data.CategoriaDto
import com.example.bodega_flow.data.ProductoCreateDto
import com.example.bodega_flow.data.ProductoDto
import com.example.bodega_flow.data.UnidadMedidaDto
import com.example.bodega_flow.network.ApiClient
import com.example.bodega_flow.data.parseHttpError
import retrofit2.HttpException

class ProductoRepository {

    private val api = ApiClient.api

    suspend fun getProductos(): List<ProductoDto> {
        return try {
            api.getProductos()
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }

    suspend fun crearProducto(body: ProductoCreateDto): ProductoDto {
        return try {
            api.crearProducto(body)
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }

    suspend fun getCategorias(): List<CategoriaDto> {
        return try {
            api.getCategorias()
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }

    suspend fun getUnidadesMedida(): List<UnidadMedidaDto> {
        return try {
            api.getUnidadesMedida()
        } catch (e: HttpException) {
            throw Exception(parseHttpError(e))
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor")
        }
    }
}
