package com.example.bodega_flow.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.data.ExistenciaDto
import com.example.bodega_flow.repository.BodegaRepository
import com.example.bodega_flow.viewmodel.BodegaViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class BodegasScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun muestra_bodegas_y_al_click_abre_dialog_con_productos() {
        val repo = mockk<BodegaRepository>()

        val bodegas = listOf(
            BodegaDto(id = 1L, nombre = "Bodega Norte", ubicacion = "Santiago", activo = true),
            BodegaDto(id = 2L, nombre = "Bodega Sur", ubicacion = null, activo = false)
        )

        val e1 = mockk<ExistenciaDto>()
        every { e1.productoNombre } returns "Guantes Nitrilo"
        every { e1.cantidad } returns 100.0

        val e2 = mockk<ExistenciaDto>()
        every { e2.productoNombre } returns "Mascarillas"
        every { e2.cantidad } returns 50.0

        val productosNorte = listOf(e1, e2)

        coEvery { repo.getBodegas() } returns bodegas
        coEvery { repo.getProductosDeBodega(1L) } returns productosNorte

        val vm = BodegaViewModel(repo)

        composeRule.setContent {
            BodegasScreen(bodegaViewModel = vm)
        }

        // Lista visible
        composeRule.onNodeWithText("Bodegas").assertExists()
        composeRule.onNodeWithText("Bodega Norte").assertExists()
        composeRule.onNodeWithText("Ubicación: Santiago").assertExists()
        composeRule.onNodeWithText("Activa: Sí").assertExists()
        composeRule.onNodeWithText("Bodega Sur").assertExists()
        composeRule.onNodeWithText("Activa: No").assertExists()

        // Click en una bodega -> abre dialog y lista productos
        composeRule.onNodeWithText("Bodega Norte").performClick()

        composeRule.onNodeWithText("Productos en Bodega Norte").assertExists()
        composeRule.onNodeWithText("Guantes Nitrilo - Cantidad: 100.0").assertExists()
        composeRule.onNodeWithText("Mascarillas - Cantidad: 50.0").assertExists()
        composeRule.onNodeWithText("Cerrar").assertExists()
    }

    @Test
    fun si_repo_falla_muestra_mensaje_de_error() {
        val repo = mockk<BodegaRepository>()
        coEvery { repo.getBodegas() } throws RuntimeException("Fallo al cargar bodegas")

        val vm = BodegaViewModel(repo)

        composeRule.setContent {
            BodegasScreen(bodegaViewModel = vm)
        }

        composeRule.onNodeWithText("Fallo al cargar bodegas").assertExists()
    }
}
