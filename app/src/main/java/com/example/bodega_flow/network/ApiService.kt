package com.example.bodega_flow.network

import com.example.bodega_flow.data.AuthResponse
import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.CategoriaDto
import com.example.bodega_flow.data.LoginRequest
import com.example.bodega_flow.data.MotivoMovimientoDto
import com.example.bodega_flow.data.MovimientoCreateDto
import com.example.bodega_flow.data.MovimientoDto
import com.example.bodega_flow.data.ProductoCreateDto
import com.example.bodega_flow.data.ProductoDto
import com.example.bodega_flow.data.RegisterRequest
import com.example.bodega_flow.data.UnidadMedidaDto
import com.example.bodega_flow.data.UsuarioDto
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): UsuarioDto

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    // Usuarios
    @GET("api/usuarios")
    suspend fun getUsuarios(): List<UsuarioDto>

    // Catálogos
    @GET("api/unidades-medida")
    suspend fun getUnidadesMedida(): List<UnidadMedidaDto>

    @GET("api/categorias")
    suspend fun getCategorias(): List<CategoriaDto>

    @GET("api/bodegas")
    suspend fun getBodegas(): List<BodegaDto>

    @GET("api/motivos")
    suspend fun getMotivosMovimiento(): List<MotivoMovimientoDto>

    // Productos
    @POST("api/productos")
    suspend fun crearProducto(@Body body: ProductoCreateDto): ProductoDto

    @GET("api/productos")
    suspend fun getProductos(): List<ProductoDto>

    // Movimientos
    @POST("api/movimientos")
    suspend fun crearMovimiento(@Body body: MovimientoCreateDto): MovimientoDto

    @POST("api/movimientos/{id}/undo")
    suspend fun deshacerMovimiento(
        @Path("id") movimientoId: Long,
        @Query("usuarioId") usuarioId: Long
    ): MovimientoDto

    @GET("api/movimientos")
    suspend fun getMovimientosPorProducto(
        @Query("productoId") productoId: Long
    ): List<MovimientoDto>
}