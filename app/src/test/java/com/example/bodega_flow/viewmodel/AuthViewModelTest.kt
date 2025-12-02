package com.example.bodega_flow.viewmodel

import com.example.bodega_flow.data.AuthResponse
import com.example.bodega_flow.data.LoginRequest
import com.example.bodega_flow.data.RegisterRequest
import com.example.bodega_flow.data.UsuarioDto
import com.example.bodega_flow.repository.AuthRepository
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
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<AuthRepository>()

    @Test
    fun `login exitoso con correo duoc y pass 6 caracteres`() = runTest {
        val vm = AuthViewModel(repo)

        val email = "andres@duoc.cl"
        val password = "123456"

        val resp = mockk<AuthResponse>()
        coEvery { repo.login(LoginRequest(email = email, password = password)) } returns resp

        var successResp: AuthResponse? = null

        vm.login(email, password) { successResp = it }
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertEquals(resp, successResp)

        coVerify(exactly = 1) { repo.login(LoginRequest(email = email, password = password)) }
    }

    @Test
    fun `login con pass incorrecta setea error y no llama onSuccess`() = runTest {
        val vm = AuthViewModel(repo)

        val email = "andres@duoc.cl"
        val password = "123456"

        coEvery { repo.login(LoginRequest(email = email, password = password)) } throws RuntimeException("Credenciales inválidas")

        var called = false
        vm.login(email, password) { called = true }
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Credenciales inválidas", vm.uiState.value.error)
        assertFalse(called)

        coVerify(exactly = 1) { repo.login(LoginRequest(email = email, password = password)) }
    }

    @Test
    fun `register exitoso con nombre real y nickname pipe`() = runTest {
        val vm = AuthViewModel(repo)

        val nombre = "Andres Escobar"
        val email = "andres@duoc.cl"
        val username = "pipe"
        val password = "123456"
        val usuarioDto = mockk<UsuarioDto>()

        coEvery {
            repo.register(
                RegisterRequest(
                    nombre = nombre,
                    email = email,
                    username = username,
                    password = password
                )
            )
        } returns usuarioDto

        var called = false

        vm.register(
            nombre = nombre,
            email = email,
            username = username,
            password = password
        ) { called = true }

        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertNull(vm.uiState.value.error)
        assertTrue(called)

        coVerify(exactly = 1) {
            repo.register(
                RegisterRequest(
                    nombre = nombre,
                    email = email,
                    username = username,
                    password = password
                )
            )
        }
    }

    @Test
    fun `register con error setea error y no ejecuta onSuccess`() = runTest {
        val vm = AuthViewModel(repo)

        val nombre = "Andres Escobar"
        val email = "andres@duoc.cl"
        val username = "pipe"
        val password = "123456"

        coEvery { repo.register(any()) } throws RuntimeException("Error al registrar")

        var called = false
        vm.register(nombre, email, username, password) { called = true }
        advanceUntilIdle()

        assertFalse(vm.uiState.value.loading)
        assertEquals("Error al registrar", vm.uiState.value.error)
        assertFalse(called)

        coVerify(exactly = 1) { repo.register(any()) }
    }

    @Test
    fun `clearError limpia mensaje de error`() = runTest {
        val vm = AuthViewModel(repo)

        val email = "andres@duoc.cl"
        val password = "123456"

        coEvery { repo.login(LoginRequest(email = email, password = password)) } throws RuntimeException("X")

        vm.login(email, password) { }
        advanceUntilIdle()

        assertNotNull(vm.uiState.value.error)

        vm.clearError()

        assertNull(vm.uiState.value.error)
    }
}
