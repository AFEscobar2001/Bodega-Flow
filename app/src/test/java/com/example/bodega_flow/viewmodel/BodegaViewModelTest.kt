package com.example.bodega_flow.viewmodel

import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.ExistenciaDto
import com.example.bodega_flow.repository.BodegaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BodegaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<BodegaRepository>()

    @Test
    fun `cargarBodegas exitoso setea bodegas y deja loading false`() = runTest {
        // Arrange
        val vm = BodegaViewModel(repo)

        val bodegas = listOf(
            mockk<BodegaDto>(),
            mockk<BodegaDto>(),
            mockk<BodegaDto>()
        )

        coEvery { repo.getBodegas() } returns bodegas

        // Act
        vm.cargarBodegas()
        advanceUntilIdle()

        // Assert
        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(bodegas, vm.uiState.value.bodegas)

        coVerify(exactly = 1) { repo.getBodegas() }
    }

    @Test
    fun `cargarBodegas con error setea error y deja loading false`() = runTest {
        // Arrange
        val vm = BodegaViewModel(repo)

        coEvery { repo.getBodegas() } throws RuntimeException("Fallo al cargar bodegas")

        // Act
        vm.cargarBodegas()
        advanceUntilIdle()

        // Assert
        assertFalse(vm.uiState.value.loading)
        assertEquals("Fallo al cargar bodegas", vm.uiState.value.error)

        coVerify(exactly = 1) { repo.getBodegas() }
    }

    @Test
    fun `cargarProductosDeBodega exitoso setea productos para bodegaId`() = runTest {
        // Arrange
        val vm = BodegaViewModel(repo)

        val bodegaId = 1L

        val productos = listOf(
            mockk<ExistenciaDto>(),
            mockk<ExistenciaDto>()
        )

        coEvery { repo.getProductosDeBodega(bodegaId) } returns productos

        // Act
        vm.cargarProductosDeBodega(bodegaId)
        advanceUntilIdle()

        // Assert
        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(productos, vm.uiState.value.productos)

        coVerify(exactly = 1) { repo.getProductosDeBodega(bodegaId) }
    }

    @Test
    fun `cargarProductosDeBodega con error setea error y deja loading false`() = runTest {
        // Arrange
        val vm = BodegaViewModel(repo)

        val bodegaId = 2L
        coEvery { repo.getProductosDeBodega(bodegaId) } throws RuntimeException("Bodega no encontrada")

        // Act
        vm.cargarProductosDeBodega(bodegaId)
        advanceUntilIdle()

        // Assert
        assertFalse(vm.uiState.value.loading)
        assertEquals("Bodega no encontrada", vm.uiState.value.error)

        coVerify(exactly = 1) { repo.getProductosDeBodega(bodegaId) }
    }
}
