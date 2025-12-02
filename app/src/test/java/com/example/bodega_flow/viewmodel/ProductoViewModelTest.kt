package com.example.bodega_flow.viewmodel

import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.CategoriaDto
import com.example.bodega_flow.data.ProductoCreateDto
import com.example.bodega_flow.data.ProductoDto
import com.example.bodega_flow.data.UnidadMedidaDto
import com.example.bodega_flow.repository.BodegaRepository
import com.example.bodega_flow.repository.ProductoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<ProductoRepository>()
    private val bodegaRepo = mockk<BodegaRepository>()

    @Test
    fun `cargarCatalogosYProductos exitoso setea productos categorias unidades bodegas`() = runTest {
        val vm = ProductoViewModel(repo, bodegaRepo)

        val productos = listOf(mockk<ProductoDto>(), mockk<ProductoDto>())
        val categorias = listOf(mockk<CategoriaDto>(), mockk<CategoriaDto>())
        val unidades = listOf(mockk<UnidadMedidaDto>())
        val bodegas = listOf(mockk<BodegaDto>(), mockk<BodegaDto>(), mockk<BodegaDto>())

        coEvery { repo.getProductos() } returns productos
        coEvery { repo.getCategorias() } returns categorias
        coEvery { repo.getUnidadesMedida() } returns unidades
        coEvery { bodegaRepo.getBodegas() } returns bodegas

        vm.cargarCatalogosYProductos()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(productos, vm.uiState.value.productos)
        assertEquals(categorias, vm.uiState.value.categorias)
        assertEquals(unidades, vm.uiState.value.unidades)
        assertEquals(bodegas, vm.uiState.value.bodegas)

        coVerify(exactly = 1) { repo.getProductos() }
        coVerify(exactly = 1) { repo.getCategorias() }
        coVerify(exactly = 1) { repo.getUnidadesMedida() }
        coVerify(exactly = 1) { bodegaRepo.getBodegas() }
    }

    @Test
    fun `cargarCatalogosYProductos con error setea error`() = runTest {
        val vm = ProductoViewModel(repo, bodegaRepo)

        coEvery { repo.getProductos() } throws RuntimeException("Fallo al cargar productos")

        vm.cargarCatalogosYProductos()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Fallo al cargar productos", vm.uiState.value.error)

        coVerify(exactly = 1) { repo.getProductos() }
        coVerify(exactly = 0) { repo.getCategorias() }
        coVerify(exactly = 0) { repo.getUnidadesMedida() }
        coVerify(exactly = 0) { bodegaRepo.getBodegas() }
    }

    @Test
    fun `crearProducto exitoso envia dto correcto y agrega producto al estado`() = runTest {
        val vm = ProductoViewModel(repo, bodegaRepo)

        // Estado inicial con 1 producto para comprobar append
        val p0 = mockk<ProductoDto>()
        coEvery { repo.getProductos() } returns listOf(p0)
        coEvery { repo.getCategorias() } returns emptyList()
        coEvery { repo.getUnidadesMedida() } returns emptyList()
        coEvery { bodegaRepo.getBodegas() } returns emptyList()

        vm.cargarCatalogosYProductos()
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.productos.size)

        val codigo = "EPP001"
        val nombre = "Guantes Nitrilo"
        val categoriaId = 1L
        val unidadId = 2L
        val cantidadInicial = 100.0
        val bodegaId = 3L

        val creado = mockk<ProductoDto>()

        val dtoSlot = slot<ProductoCreateDto>()
        coEvery { repo.crearProducto(capture(dtoSlot)) } returns creado

        vm.crearProducto(
            codigo = codigo,
            nombre = nombre,
            categoriaId = categoriaId,
            unidadId = unidadId,
            cantidadInicial = cantidadInicial,
            bodegaId = bodegaId
        )
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(2, vm.uiState.value.productos.size)
        assertSame(creado, vm.uiState.value.productos.last())

        // DTO construido por el ViewModel
        assertEquals(codigo, dtoSlot.captured.codigo)
        assertEquals(nombre, dtoSlot.captured.nombre)
        assertEquals(categoriaId, dtoSlot.captured.categoriaId)
        assertEquals(unidadId, dtoSlot.captured.unidadMedidaId)
        assertEquals(cantidadInicial, dtoSlot.captured.cantidadInicial, 0.0)
        assertEquals(bodegaId, dtoSlot.captured.bodegaId)

        coVerify(exactly = 1) { repo.crearProducto(any()) }
    }

    @Test
    fun `crearProducto con error setea error y no agrega producto`() = runTest {
        val vm = ProductoViewModel(repo, bodegaRepo)

        coEvery { repo.crearProducto(any()) } throws RuntimeException("Código duplicado")

        vm.crearProducto(
            codigo = "EPP001",
            nombre = "Guantes Nitrilo",
            categoriaId = 1L,
            unidadId = 2L,
            cantidadInicial = 100.0,
            bodegaId = 3L
        )
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Código duplicado", vm.uiState.value.error)
        assertEquals(0, vm.uiState.value.productos.size)

        coVerify(exactly = 1) { repo.crearProducto(any()) }
    }

    @Test
    fun `clearError limpia mensaje de error`() = runTest {
        val vm = ProductoViewModel(repo, bodegaRepo)

        coEvery { repo.getProductos() } throws RuntimeException("X")

        vm.cargarCatalogosYProductos()
        advanceUntilIdle()

        assertNotNull(vm.uiState.value.error)

        vm.clearError()

        assertNull(vm.uiState.value.error)
    }
}
