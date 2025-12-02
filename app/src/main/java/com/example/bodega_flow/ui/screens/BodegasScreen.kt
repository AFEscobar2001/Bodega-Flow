package com.example.bodega_flow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodega_flow.data.BodegaDto
import com.example.bodega_flow.viewmodel.BodegaViewModel

@Composable
fun BodegasScreen(
    bodegaViewModel: BodegaViewModel = viewModel()
) {
    val uiState by bodegaViewModel.uiState.collectAsState()
    var bodegaSeleccionada by remember { mutableStateOf<BodegaDto?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bodegaViewModel.cargarBodegas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bodegas", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.loading) CircularProgressIndicator()

        uiState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.bodegas.size) { i ->
                val b = uiState.bodegas[i]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            bodegaSeleccionada = b
                            bodegaViewModel.cargarProductosDeBodega(b.id)
                            showDialog = true
                        },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(b.nombre, style = MaterialTheme.typography.bodyLarge)
                        if (!b.ubicacion.isNullOrBlank()) {
                            Text("Ubicación: ${b.ubicacion}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            "Activa: ${if (b.activo) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (showDialog && bodegaSeleccionada != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Productos en ${bodegaSeleccionada!!.nombre}") },
            text = {
                if (uiState.productos.isEmpty()) {
                    Text("No hay productos asignados.")
                } else {
                    Column {
                        uiState.productos.forEach { e ->
                            Text("${e.productoNombre} - Cantidad: ${e.cantidad}")
                        }
                    }
                }
            }
        )
    }
}
