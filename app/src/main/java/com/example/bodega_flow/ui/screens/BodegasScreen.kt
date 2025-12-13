package com.example.bodega_flow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodega_flow.viewmodel.BodegaViewModel

@Composable
fun BodegasScreen(
    openBodegaId: Long? = null,
    bodegaViewModel: BodegaViewModel = viewModel()
) {
    val uiState by bodegaViewModel.uiState.collectAsState()

    var selectedBodegaId by remember { mutableStateOf<Long?>(null) }
    var showDetalle by remember { mutableStateOf(false) }

    // carga bodegas al entrar
    LaunchedEffect(Unit) {
        bodegaViewModel.cargarBodegas()
    }

    // si viene desde QR, auto-selecciona y abre detalle
    LaunchedEffect(openBodegaId, uiState.bodegas) {
        val id = openBodegaId
        if (id != null && uiState.bodegas.any { it.id == id }) {
            selectedBodegaId = id
            showDetalle = true
            bodegaViewModel.cargarProductosDeBodega(id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bodegas", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.loading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
        }

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.bodegas.isEmpty() && !uiState.loading) {
            Text("No hay bodegas.")
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.bodegas) { b ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            selectedBodegaId = b.id
                            showDetalle = true
                            bodegaViewModel.cargarProductosDeBodega(b.id)
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(b.nombre, style = MaterialTheme.typography.bodyLarge)
                        b.ubicacion?.takeIf { it.isNotBlank() }?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    // Detalle de bodega: lista de productos asignados (existencias)
    if (showDetalle && selectedBodegaId != null) {
        val bodegaNombre = uiState.bodegas.firstOrNull { it.id == selectedBodegaId }?.nombre ?: "Bodega"

        AlertDialog(
            onDismissRequest = { showDetalle = false },
            confirmButton = {
                TextButton(onClick = { showDetalle = false }) { Text("Cerrar") }
            },
            title = { Text("Detalle: $bodegaNombre") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (uiState.loading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cargando productos...")
                        }
                        return@Column
                    }

                    if (uiState.productos.isEmpty()) {
                        Text("Esta bodega no tiene productos asignados.")
                        return@Column
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                    ) {
                        items(uiState.productos) { ex ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Producto ID: ${ex.productoId}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Cantidad: ${ex.cantidad}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
