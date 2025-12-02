package com.example.bodega_flow.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AuthStartScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun muestra_textos_y_ejecuta_callbacks() {
        var loginClicks = 0
        var registerClicks = 0

        composeRule.setContent {
            AuthStartScreen(
                onLoginClick = { loginClicks++ },
                onRegisterClick = { registerClicks++ }
            )
        }

        // Verifica textos visibles
        composeRule.onNodeWithText("BodegaFlow").assertExists()
        composeRule.onNodeWithText("Control simple de EPP e inventario").assertExists()
        composeRule.onNodeWithText("Iniciar sesión").assertExists()
        composeRule.onNodeWithText("Registrarse").assertExists()

        // Interacción
        composeRule.onNodeWithText("Iniciar sesión").performClick()
        composeRule.onNodeWithText("Registrarse").performClick()

        // Verificación callbacks
        assertEquals(1, loginClicks)
        assertEquals(1, registerClicks)
    }
}
