package com.example.bodega_flow.viewmodel

import com.example.bodega_flow.data.UsuarioDto
import com.example.bodega_flow.data.UsuarioUpdateRequest
import com.example.bodega_flow.repository.UsuarioRepository
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
class PerfilViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<UsuarioRepository>()

    @Test
    fun `cargarUsuario exitoso setea usuario y deja loading false`() = runTest {
        val vm = PerfilViewModel(repo)

        val usuario = mockk<UsuarioDto>()
        coEvery { repo.getUsuario(10L) } returns usuario

        vm.cargarUsuario(10L)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(usuario, vm.uiState.value.usuario)

        coVerify(exactly = 1) { repo.getUsuario(10L) }
    }

    @Test
    fun `cargarUsuario con error setea error`() = runTest {
        val vm = PerfilViewModel(repo)

        coEvery { repo.getUsuario(10L) } throws RuntimeException("Usuario no encontrado")

        vm.cargarUsuario(10L)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Usuario no encontrado", vm.uiState.value.error)

        coVerify(exactly = 1) { repo.getUsuario(10L) }
    }

    @Test
    fun `actualizarUsuario exitoso actualiza usuario y deja loading false`() = runTest {
        val vm = PerfilViewModel(repo)

        val req = UsuarioUpdateRequest(
            nombre = "Andres Escobar",
            email = "andres@duoc.cl",
            username = "pipe"
        )

        val usuarioActualizado = mockk<UsuarioDto>()
        coEvery { repo.actualizarUsuario(10L, req) } returns usuarioActualizado

        vm.actualizarUsuario(10L, req)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(usuarioActualizado, vm.uiState.value.usuario)

        coVerify(exactly = 1) { repo.actualizarUsuario(10L, req) }
    }

    @Test
    fun `actualizarUsuario con error setea error`() = runTest {
        val vm = PerfilViewModel(repo)

        val req = UsuarioUpdateRequest(
            nombre = "Andres Escobar",
            email = "andres@duoc.cl",
            username = "pipe"
        )

        coEvery { repo.actualizarUsuario(10L, req) } throws RuntimeException("Error al actualizar")

        vm.actualizarUsuario(10L, req)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Error al actualizar", vm.uiState.value.error)

        coVerify(exactly = 1) { repo.actualizarUsuario(10L, req) }
    }

    @Test
    fun `clearError limpia mensaje de error`() = runTest {
        val vm = PerfilViewModel(repo)

        coEvery { repo.getUsuario(any()) } throws RuntimeException("X")

        vm.cargarUsuario(1L)
        advanceUntilIdle()

        assertNotNull(vm.uiState.value.error)

        vm.clearError()

        assertNull(vm.uiState.value.error)
    }
}
