package com.example.bodega_flow.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.bodega_flow.data.RegisterRequest
import com.example.bodega_flow.data.UsuarioDto
import com.example.bodega_flow.repository.AuthRepository
import com.example.bodega_flow.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun click_en_volver_llama_onBack() {
        var backCalls = 0

        composeRule.setContent {
            RegisterScreen(
                onRegisterSuccess = {},
                onBack = { backCalls++ }
            )
        }

        composeRule.onNodeWithText("Volver").performClick()

        assertEquals(1, backCalls)
    }

    @Test
    fun register_exitoso_llama_onRegisterSuccess() {
        val repo = mockk<AuthRepository>()

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

        val vm = AuthViewModel(repo)

        var successCalls = 0

        composeRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { successCalls++ },
                onBack = {},
                authViewModel = vm
            )
        }

        composeRule.onNodeWithText("Nombre").performTextInput(nombre)
        composeRule.onNodeWithText("Correo").performTextInput(email)
        composeRule.onNodeWithText("Usuario").performTextInput(username)
        composeRule.onNodeWithText("Contraseña").performTextInput(password)

        composeRule.onNodeWithText("Crear cuenta").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { successCalls == 1 }

        assertEquals(1, successCalls)
    }

    @Test
    fun register_fallido_muestra_error_en_pantalla() {
        val repo = mockk<AuthRepository>()

        coEvery { repo.register(any()) } throws RuntimeException("Error al registrar")

        val vm = AuthViewModel(repo)

        composeRule.setContent {
            RegisterScreen(
                onRegisterSuccess = {},
                onBack = {},
                authViewModel = vm
            )
        }

        composeRule.onNodeWithText("Nombre").performTextInput("Andres Escobar")
        composeRule.onNodeWithText("Correo").performTextInput("andres@duoc.cl")
        composeRule.onNodeWithText("Usuario").performTextInput("pipe")
        composeRule.onNodeWithText("Contraseña").performTextInput("123456")

        composeRule.onNodeWithText("Crear cuenta").performClick()

        composeRule.onNodeWithText("Error al registrar").assertExists()
    }
}
