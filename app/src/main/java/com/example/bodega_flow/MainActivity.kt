package com.example.bodega_flow

import QrScannerScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bodega_flow.data.SessionManager
import com.example.bodega_flow.ui.screens.AuthStartScreen
import com.example.bodega_flow.ui.screens.BodegasScreen
import com.example.bodega_flow.ui.screens.HomeScreen
import com.example.bodega_flow.ui.screens.LoginScreen
import com.example.bodega_flow.ui.screens.RegisterScreen
import com.example.bodega_flow.ui.theme.BodegaFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BodegaFlowTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    BodegaApp()
                }
            }
        }
    }
}

@Composable
fun BodegaApp() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val startDestination = if (sessionManager.getUser() != null) {
        "home"
    } else {
        "auth_start"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("auth_start") {
            AuthStartScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth_start") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("auth_start") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("scanner") {
            QrScannerScreen(
                onQrDetected = { valorQr ->
                    navController.navigate("productDetail/$valorQr")
                },
                onBack = { navController.popBackStack() }
            )
        }

    }
}
