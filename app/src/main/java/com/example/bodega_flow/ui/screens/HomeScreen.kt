package com.example.bodega_flow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.bodega_flow.data.SessionManager
import androidx.compose.material3.TopAppBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodega_flow.viewmodel.ProductoViewModel
import androidx.compose.foundation.lazy.LazyRow
import com.example.bodega_flow.viewmodel.MovimientoViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


private enum class HomeSection {
    PRODUCTOS, MOVIMIENTOS, PERFIL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSection by remember { mutableStateOf(HomeSection.PRODUCTOS) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "BodegaFlow",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                DrawerItem(
                    label = "Productos",
                    selected = selectedSection == HomeSection.PRODUCTOS,
                    onClick = {
                        selectedSection = HomeSection.PRODUCTOS
                        scope.launch { drawerState.close() }
                    }
                )

                DrawerItem(
                    label = "Movimientos / Historia",
                    selected = selectedSection == HomeSection.MOVIMIENTOS,
                    onClick = {
                        selectedSection = HomeSection.MOVIMIENTOS
                        scope.launch { drawerState.close() }
                    }
                )

                DrawerItem(
                    label = "Perfil",
                    selected = selectedSection == HomeSection.PERFIL,
                    onClick = {
                        selectedSection = HomeSection.PERFIL
                        scope.launch { drawerState.close() }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Divider()

                DrawerItem(
                    label = "Cerrar sesión",
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (selectedSection) {
                                HomeSection.PRODUCTOS -> "Productos"
                                HomeSection.MOVIMIENTOS -> "Movimientos / Historia"
                                HomeSection.PERFIL -> "Perfil"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedSection) {
                    HomeSection.PRODUCTOS -> ProductosScreen()
                    HomeSection.MOVIMIENTOS -> MovimientosScreen()
                    HomeSection.PERFIL -> PerfilScreen()
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background =
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProductosScreen(
    productoViewModel: ProductoViewModel = viewModel()
) {
    val uiState by productoViewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var categoriaSeleccionadaId by remember { mutableStateOf<Long?>(null) }
    var unidadSeleccionadaId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        productoViewModel.cargarCatalogosYProductos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Productos", style = MaterialTheme.typography.titleMedium)

            Button(onClick = { showDialog = true }) {
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.loading) {
            CircularProgressIndicator()
        }

        uiState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.productos.size) { index ->
                val p = uiState.productos[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(text = p.nombre, style = MaterialTheme.typography.bodyLarge)
                        Text("Código: ${p.codigo}", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Categoría: ${p.categoriaId}  U.M.: ${p.unidadMedidaId}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    // ---------- DIÁLOGO DE CREAR PRODUCTO ----------
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val catId = categoriaSeleccionadaId
                    val uId = unidadSeleccionadaId
                    if (codigo.isNotBlank() && nombre.isNotBlank() && catId != null && uId != null) {
                        productoViewModel.crearProducto(
                            codigo = codigo,
                            nombre = nombre,
                            categoriaId = catId,
                            unidadId = uId
                        )
                        showDialog = false
                        codigo = ""
                        nombre = ""
                        categoriaSeleccionadaId = null
                        unidadSeleccionadaId = null
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Nuevo producto") },
            text = {
                Column {
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = {
                            codigo = it
                            productoViewModel.clearError()
                        },
                        label = { Text("Código") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            productoViewModel.clearError()
                        },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ---------- CATEGORIAS ----------
                    if (uiState.categorias.isNotEmpty()) {
                        Text("Categoría:")
                        LazyRow {
                            items(uiState.categorias.size) { i ->
                                val c = uiState.categorias[i]
                                val selected = categoriaSeleccionadaId == c.id

                                AssistChip(
                                    onClick = { categoriaSeleccionadaId = c.id },
                                    label = { Text(c.nombre) },
                                    modifier = Modifier.padding(end = 4.dp),
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor =
                                            if (selected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surface,
                                        labelColor =
                                            if (selected) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ---------- UNIDADES ----------
                    if (uiState.unidades.isNotEmpty()) {
                        Text("Unidad:")
                        LazyRow {
                            items(uiState.unidades.size) { i ->
                                val u = uiState.unidades[i]
                                val selected = unidadSeleccionadaId == u.id

                                AssistChip(
                                    onClick = { unidadSeleccionadaId = u.id },
                                    label = { Text(u.codigo) },
                                    modifier = Modifier.padding(end = 4.dp),
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor =
                                            if (selected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surface,
                                        labelColor =
                                            if (selected) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}


@Composable
fun MovimientosScreen(
    movimientoViewModel: MovimientoViewModel = viewModel(),
    productoViewModel: ProductoViewModel = viewModel()
) {
    val movState by movimientoViewModel.uiState.collectAsState()
    val prodState by productoViewModel.uiState.collectAsState()

    var productoSeleccionadoId by remember { mutableStateOf<Long?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var cantidad by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("IN") }

    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val usuarioId = session.getUser()?.id ?: return

    LaunchedEffect(Unit) {
        productoViewModel.cargarCatalogosYProductos()
        movimientoViewModel.cargarCatalogos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Movimientos / Historia", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // ---------- SELECTOR DE PRODUCTO ----------
        if (prodState.productos.isNotEmpty()) {
            Text(
                "Producto: " +
                        (prodState.productos.find { it.id == productoSeleccionadoId }?.nombre ?: "-")
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow {
                items(prodState.productos.size) { i ->
                    val p = prodState.productos[i]
                    val selected = productoSeleccionadoId == p.id

                    AssistChip(
                        onClick = {
                            productoSeleccionadoId = p.id
                            movimientoViewModel.cargarMovimientos(p.id)
                        },
                        label = { Text(p.nombre) },
                        modifier = Modifier.padding(end = 4.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor =
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface,
                            labelColor =
                                if (selected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { if (productoSeleccionadoId != null) showDialog = true },
            enabled = productoSeleccionadoId != null
        ) {
            Text("Nuevo movimiento")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (movState.loading) CircularProgressIndicator()

        movState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (movState.movimientos.isEmpty() && productoSeleccionadoId != null && !movState.loading) {
            Text("Sin movimientos para este producto.")
        }

        // ---------- LISTA ----------
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(movState.movimientos.size) { index ->
                val m = movState.movimientos[index]

                // De momento mostramos solo IDs porque el DTO no tiene nombre/descripcion
                val motivoTexto = "Motivo ID: ${m.motivoId}"
                val bodegaTexto = "Bodega ID: ${m.bodegaId}"

                val fechaFormateada = try {
                    OffsetDateTime.parse(m.createdAt)
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                } catch (e: Exception) {
                    m.createdAt
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Tipo: ${m.tipo}")
                        Text("Cantidad: ${m.cantidad}")
                        Text(motivoTexto)
                        Text(bodegaTexto)
                        Text("Fecha: $fechaFormateada")
                    }
                }
            }
        }
    }

    // ---------- DIÁLOGO ----------
    if (showDialog && productoSeleccionadoId != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val cant = cantidad.toDoubleOrNull()
                    val motivoId = movState.motivos.firstOrNull()?.id ?: 1L
                    val bodegaId = movState.bodegas.firstOrNull()?.id ?: 1L

                    if (cant != null) {
                        movimientoViewModel.crearMovimiento(
                            productoId = productoSeleccionadoId!!,
                            bodegaId = bodegaId,
                            usuarioId = usuarioId,
                            motivoId = motivoId,
                            tipo = tipo,
                            cantidad = cant,
                            comentario = comentario.ifBlank { null }
                        )
                        showDialog = false
                        cantidad = ""
                        comentario = ""
                        tipo = "IN"
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Nuevo movimiento") },
            text = {
                Column {
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentario (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Tipo:")

                    Row {
                        listOf("IN", "OUT", "ADJUST").forEach { t ->
                            val selected = tipo == t

                            AssistChip(
                                onClick = { tipo = t },
                                label = { Text(t) },
                                modifier = Modifier.padding(end = 4.dp),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor =
                                        if (selected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surface,
                                    labelColor =
                                        if (selected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}



@Composable
fun PerfilScreen() {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val user = remember { session.getUser() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Perfil", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (user != null) {
            Text("ID: ${user.id}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nombre: ${user.nombre}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Usuario: ${user.username}")
        } else {
            Text("No hay usuario en sesión.")
        }
    }
}
