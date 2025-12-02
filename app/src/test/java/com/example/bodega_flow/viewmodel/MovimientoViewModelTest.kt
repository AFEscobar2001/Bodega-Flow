package com.example.bodega_flow.viewmodel

import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.MovimientoCreateDto
import com.example.bodega_flow.data.MovimientoDto
import com.example.bodega_flow.repository.BodegaRepository
import com.example.bodega_flow.repository.MovimientoRepository
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
class MovimientoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<MovimientoRepository>()
    private val bodegaRepo = mockk<BodegaRepository>()

    @Test
    fun `cargarCatalogos exitoso setea bodegas y deja loading false`() = runTest {
        val vm = MovimientoViewModel(repo, bodegaRepo)

        val bodegas = listOf(
            mockk<BodegaDto>(),
            mockk<BodegaDto>()
        )
        coEvery { bodegaRepo.getBodegas() } returns bodegas

        vm.cargarCatalogos()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(bodegas, vm.uiState.value.bodegas)

        coVerify(exactly = 1) { bodegaRepo.getBodegas() }
    }

    @Test
    fun `cargarCatalogos con error setea error`() = runTest {
        val vm = MovimientoViewModel(repo, bodegaRepo)

        coEvery { bodegaRepo.getBodegas() } throws RuntimeException("Fallo catálogo")

        vm.cargarCatalogos()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Fallo catálogo", vm.uiState.value.error)

        coVerify(exactly = 1) { bodegaRepo.getBodegas() }
    }

    @Test
    fun `cargarMovimientos exitoso setea lista para producto`() = runTest {
        val vm = MovimientoViewModel(repo, bodegaRepo)

        val productoId = 100L
        val movimientos = listOf(
            mockk<MovimientoDto>(),
            mockk<MovimientoDto>()
        )
        coEvery { repo.getMovimientosPorProducto(productoId) } returns movimientos

        vm.cargarMovimientos(productoId)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(movimientos, vm.uiState.value.movimientos)

        coVerify(exactly = 1) { repo.getMovimientosPorProducto(productoId) }
    }

    @Test
    fun `cargarMovimientos con error setea error`() = runTest {
        val vm = MovimientoViewModel(repo, bodegaRepo)

        val productoId = 101L
        coEvery { repo.getMovimientosPorProducto(productoId) } throws RuntimeException("Sin conexión")

        vm.cargarMovimientos(productoId)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Sin conexión", vm.uiState.value.error)

        coVerify(exactly = 1) { repo.getMovimientosPorProducto(productoId) }
    }

    @Test
    fun `crearMovimiento exitoso envia dto correcto y agrega movimiento al estado`() = runTest {
        val vm = MovimientoViewModel(repo, bodegaRepo)

        // Estado inicial con 1 movimiento
        val m0 = mockk<MovimientoDto>()
        coEvery { repo.getMovimientosPorProducto(200L) } returns listOf(m0)
        vm.cargarMovimientos(200L)
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.movimientos.size)

        val productoId = 200L
        val bodegaOrigenId = 1L
        val bodegaDestinoId = 2L
        val usuarioId = 10L
        val tipo = "TRASLADO"
        val cantidad = 5.0
        val comentario = "Traslado a bodega sur"

        val creado = mockk<MovimientoDto>()

        val dtoSlot = slot<MovimientoCreateDto>()
        coEvery { repo.crearMovimiento(capture(dtoSlot)) } returns creado

        vm.crearMovimiento(
            productoId = productoId,
            bodegaOrigenId = bodegaOrigenId,
            bodegaDestinoId = bodegaDestinoId,
            usuarioId = usuarioId,
            tipo = tipo,
            cantidad = cantidad,
            comentario = comentario
        )
        advanceUntilIdle()

        // Verifica estado final
        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(2, vm.uiState.value.movimientos.size)
        assertSame(creado, vm.uiState.value.movimientos.last())

        // Verifica DTO enviado
        assertEquals(productoId, dtoSlot.captured.productoId)
        assertEquals(bodegaOrigenId, dtoSlot.captured.bodegaOrigenId)
        assertEquals(bodegaDestinoId, dtoSlot.captured.bodegaDestinoId)
        assertEquals(usuarioId, dtoSlot.captured.usuarioId)
        assertEquals(tipo, dtoSlot.captured.tipo)
        assertEquals(cantidad, dtoSlot.captured.cantidad, 0.0)
        assertEquals(comentario, dtoSlot.captured.comentario)

        coVerify(exactly = 1) { repo.crearMovimiento(any()) }
    }

    @Test
    fun `crearMovimiento con error setea error y no agrega movimiento`() = runTest {
        val vm = MovimientoViewModel(repo, bodegaRepo)

        // Estado inicial vacío
        assertEquals(0, vm.uiState.value.movimientos.size)

        coEvery { repo.crearMovimiento(any()) } throws RuntimeException("Stock insuficiente")

        vm.crearMovimiento(
            productoId = 300L,
            bodegaOrigenId = 1L,
            bodegaDestinoId = null,
            usuarioId = 10L,
            tipo = "RETIRO",
            cantidad = 9999.0,
            comentario = "Intento inválido"
        )
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Stock insuficiente", vm.uiState.value.error)
        assertEquals(0, vm.uiState.value.movimientos.size)

        coVerify(exactly = 1) { repo.crearMovimiento(any()) }
    }
}
