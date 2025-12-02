package com.example.bodega_flow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodega_flow.data.AuthResponse
import com.example.bodega_flow.data.SessionManager
import com.example.bodega_flow.data.UsuarioUpdateRequest
import com.example.bodega_flow.viewmodel.MovimientoViewModel
import com.example.bodega_flow.viewmodel.PerfilViewModel
import com.example.bodega_flow.viewmodel.ProductoViewModel
import kotlinx.coroutines.launch


private enum class HomeSection {
    PRODUCTOS, MOVIMIENTOS, BODEGAS, PERFIL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSection by remember { mutableStateOf(HomeSection.PRODUCTOS) }

    val context = LocalContext.current
    val sessionDebug = remember { SessionManager(context).getUser() }

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
                    label = "Bodegas",
                    selected = selectedSection == HomeSection.BODEGAS,
                    onClick = {
                        selectedSection = HomeSection.BODEGAS
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
                                HomeSection.BODEGAS -> "Bodegas"
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // DEBUG DE SESIÓN
                Text(
                    text = "Sesión actual: " +
                            (sessionDebug?.id?.toString() ?: "null") + " / " +
                            (sessionDebug?.nombre ?: "sin nombre") + " / " +
                            (sessionDebug?.username ?: "sin username"),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    when (selectedSection) {
                        HomeSection.PRODUCTOS -> ProductosScreen()
                        HomeSection.MOVIMIENTOS -> MovimientosScreen()
                        HomeSection.BODEGAS -> BodegasScreen()
                        HomeSection.PERFIL -> PerfilScreen()
                    }
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
    var cantidadInicial by remember { mutableStateOf("") }
    var categoriaSeleccionadaId by remember { mutableStateOf<Long?>(null) }
    var unidadSeleccionadaId by remember { mutableStateOf<Long?>(null) }
    var bodegaSeleccionadaId by remember { mutableStateOf<Long?>(null) }

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

        if (uiState.loading) CircularProgressIndicator()

        uiState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
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
                            "Categoría: ${p.categoriaNombre ?: p.categoriaId}  U.M.: ${p.unidadMedidaCodigo ?: p.unidadMedidaId}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val catId = categoriaSeleccionadaId
                    val uId = unidadSeleccionadaId
                    val bId = bodegaSeleccionadaId
                    val cant = cantidadInicial.toDoubleOrNull()

                    if (codigo.isNotBlank()
                        && nombre.isNotBlank()
                        && catId != null
                        && uId != null
                        && bId != null
                        && cant != null
                    ) {
                        productoViewModel.crearProducto(
                            codigo = codigo,
                            nombre = nombre,
                            categoriaId = catId,
                            unidadId = uId,
                            cantidadInicial = cant,
                            bodegaId = bId
                        )
                        showDialog = false
                        codigo = ""
                        nombre = ""
                        cantidadInicial = ""
                        categoriaSeleccionadaId = null
                        unidadSeleccionadaId = null
                        bodegaSeleccionadaId = null
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

                    OutlinedTextField(
                        value = cantidadInicial,
                        onValueChange = {
                            cantidadInicial = it
                            productoViewModel.clearError()
                        },
                        label = { Text("Cantidad inicial") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.bodegas.isNotEmpty()) {
                        Text("Bodega inicial:")
                        LazyRow {
                            items(uiState.bodegas.size) { i ->
                                val b = uiState.bodegas[i]
                                val selected = bodegaSeleccionadaId == b.id
                                AssistChip(
                                    onClick = { bodegaSeleccionadaId = b.id },
                                    label = { Text(b.nombre) },
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
    var tipo by remember { mutableStateOf("VENTA") }
    var bodegaOrigenSeleccionadaId by remember { mutableStateOf<Long?>(null) }
    var bodegaDestinoSeleccionadaId by remember { mutableStateOf<Long?>(null) }

    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val sessionUser = remember { session.getUser() }

    LaunchedEffect(Unit) {
        productoViewModel.cargarCatalogosYProductos()
        movimientoViewModel.cargarCatalogos()
    }

    if (sessionUser == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("No hay usuario en sesión.", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    val usuarioId = sessionUser.id

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Movimientos / Historia", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (prodState.productos.isNotEmpty()) {
            Text(
                "Producto: " +
                        (prodState.productos.find { it.id == productoSeleccionadoId }?.nombre
                            ?: "-")
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
        } else {
            Text("No hay productos cargados.")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (productoSeleccionadoId != null) {
                    bodegaOrigenSeleccionadaId = movState.bodegas.firstOrNull()?.id
                    bodegaDestinoSeleccionadaId = null
                    tipo = "VENTA"
                    cantidad = ""
                    comentario = ""
                    showDialog = true
                }
            },
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

        if (movState.movimientos.isEmpty()
            && productoSeleccionadoId != null
            && !movState.loading
        ) {
            Text("Sin movimientos para este producto.")
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(movState.movimientos.size) { index ->
                val m = movState.movimientos[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Tipo: ${m.tipo}")
                        Text("Producto: ${m.productoNombre}")
                        Text("Bodega: ${m.bodegaNombre}")
                        Text("Cantidad: ${m.cantidad}")
                        Text("Fecha: ${m.createdAt}")
                        m.comentario?.takeIf { it.isNotBlank() }?.let {
                            Text("Comentario: $it")
                        }
                    }
                }
            }
        }
    }

    if (showDialog && productoSeleccionadoId != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val cant = cantidad.toDoubleOrNull()
                    val origenId = bodegaOrigenSeleccionadaId
                    val destinoId = bodegaDestinoSeleccionadaId

                    val trasladoValido =
                        tipo == "VENTA" ||
                                (tipo == "TRASLADO" && destinoId != null && destinoId != origenId)

                    if (cant != null && origenId != null && trasladoValido) {
                        movimientoViewModel.crearMovimiento(
                            productoId = productoSeleccionadoId!!,
                            bodegaOrigenId = origenId,
                            bodegaDestinoId = if (tipo == "TRASLADO") destinoId else null,
                            usuarioId = usuarioId,
                            tipo = tipo,
                            cantidad = cant,
                            comentario = comentario.ifBlank { null }
                        )
                        showDialog = false
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
                        listOf("VENTA", "TRASLADO").forEach { t ->
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

                    Spacer(modifier = Modifier.height(8.dp))

                    if (movState.bodegas.isNotEmpty()) {
                        Text("Bodega origen:")
                        LazyRow {
                            items(movState.bodegas.size) { i ->
                                val b = movState.bodegas[i]
                                val selected = bodegaOrigenSeleccionadaId == b.id
                                AssistChip(
                                    onClick = { bodegaOrigenSeleccionadaId = b.id },
                                    label = { Text(b.nombre) },
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

                    if (tipo == "TRASLADO" && movState.bodegas.size > 1) {
                        Text("Bodega destino:")
                        LazyRow {
                            items(movState.bodegas.size) { i ->
                                val b = movState.bodegas[i]
                                val selected = bodegaDestinoSeleccionadaId == b.id
                                AssistChip(
                                    onClick = { bodegaDestinoSeleccionadaId = b.id },
                                    label = { Text(b.nombre) },
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
fun PerfilScreen(
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val sessionUser = remember { session.getUser() }

    if (sessionUser == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("No hay usuario en sesión.", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    val uiState by perfilViewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf(sessionUser.nombre) }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf(sessionUser.username) }

    LaunchedEffect(Unit) {
        perfilViewModel.cargarUsuario(sessionUser.id)
    }

    LaunchedEffect(uiState.usuario) {
        uiState.usuario?.let { u ->
            nombre = u.nombre
            email = u.email
            username = u.username
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Perfil", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.loading) {
            CircularProgressIndicator()
        }

        uiState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                perfilViewModel.clearError()
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                perfilViewModel.clearError()
            },
            label = { Text("Correo (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                perfilViewModel.clearError()
            },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val req = UsuarioUpdateRequest(
                    nombre = nombre,
                    email = email.ifBlank { null },
                    username = username
                )
                perfilViewModel.actualizarUsuario(sessionUser.id, req)

                val updated = AuthResponse(
                    id = sessionUser.id,
                    nombre = nombre,
                    username = username
                )
                session.saveSession(updated)
            },
            enabled = !uiState.loading
        ) {
            Text("Guardar cambios")
        }
    }
}
