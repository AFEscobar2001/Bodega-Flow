package com.example.bodega_flow.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.bodega_flow.data.AuthResponse
import com.example.bodega_flow.data.LoginRequest
import com.example.bodega_flow.repository.AuthRepository
import com.example.bodega_flow.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun click_en_volver_llama_onBack() {
        var backCalls = 0

        composeRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                onBack = { backCalls++ }
            )
        }

        composeRule.onNodeWithText("Volver").performClick()

        assertEquals(1, backCalls)
    }

    @Test
    fun login_exitoso_llama_onLoginSuccess() {
        val repo = mockk<AuthRepository>()

        val resp = AuthResponse(
            id = 1L,
            nombre = "Andres Escobar",
            username = "pipe"
        )

        coEvery {
            repo.login(LoginRequest(email = "andres@duoc.cl", password = "123456"))
        } returns resp

        val vm = AuthViewModel(repo)

        var successCalls = 0

        composeRule.setContent {
            LoginScreen(
                onLoginSuccess = { successCalls++ },
                onBack = {},
                authViewModel = vm
            )
        }

        composeRule.onNodeWithText("Correo").performTextInput("andres@duoc.cl")
        composeRule.onNodeWithText("Contraseña").performTextInput("123456")
        composeRule.onNodeWithText("Entrar").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { successCalls == 1 }

        assertEquals(1, successCalls)
    }

    @Test
    fun login_fallido_muestra_error_en_pantalla() {
        val repo = mockk<AuthRepository>()
        coEvery { repo.login(any()) } throws RuntimeException("Credenciales inválidas")

        val vm = AuthViewModel(repo)

        composeRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                onBack = {},
                authViewModel = vm
            )
        }

        composeRule.onNodeWithText("Correo").performTextInput("andres@duoc.cl")
        composeRule.onNodeWithText("Contraseña").performTextInput("123456")
        composeRule.onNodeWithText("Entrar").performClick()

        composeRule.onNodeWithText("Credenciales inválidas").assertExists()
    }
}
