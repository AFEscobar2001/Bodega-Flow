package com.example.bodega_flow.network

import com.example.bodega_flow.data.AuthResponse
import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.CategoriaDto
import com.example.bodega_flow.data.ExistenciaDto
import com.example.bodega_flow.data.LoginRequest
import com.example.bodega_flow.data.MovimientoCreateDto
import com.example.bodega_flow.data.MovimientoDto
import com.example.bodega_flow.data.ProductoCreateDto
import com.example.bodega_flow.data.ProductoDto
import com.example.bodega_flow.data.RegisterRequest
import com.example.bodega_flow.data.UnidadMedidaDto
import com.example.bodega_flow.data.UsuarioDto
import com.example.bodega_flow.data.UsuarioUpdateRequest
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

    @GET("api/bodegas/{id}/productos")
    suspend fun getProductosDeBodega(@Path("id") id: Long): List<ExistenciaDto>

    // Productos
    @GET("api/productos")
    suspend fun getProductos(): List<ProductoDto>

    @POST("api/productos")
    suspend fun crearProducto(@Body dto: ProductoCreateDto): ProductoDto

    // Movimientos
    @POST("api/movimientos")
    suspend fun crearMovimiento(@Body body: MovimientoCreateDto): MovimientoDto

    @GET("api/movimientos")
    suspend fun getMovimientosPorProducto(
        @Query("productoId") productoId: Long
    ): List<MovimientoDto>

    @GET("api/usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long): UsuarioDto

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body body: UsuarioUpdateRequest
    ): UsuarioDto

}