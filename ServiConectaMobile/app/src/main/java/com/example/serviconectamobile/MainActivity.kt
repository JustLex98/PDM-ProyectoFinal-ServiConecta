package com.example.serviconectamobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.serviconectamobile.ui.*
import com.example.serviconectamobile.ui.theme.ServiConectaMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServiConectaMobileTheme {
                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = "catalog"
                    ) {
                        // 1. Catálogo Principal (Público)
                        composable("catalog") {
                            CatalogScreen(
                                onLoginClick = { navController.navigate("login") },
                                onCategoryClick = { id -> navController.navigate("contractors/$id") },
                                onProfileClick = { navController.navigate("profile") },
                                onMessagesClick = { navController.navigate("messages") } // <--- RUTA DE MENSAJES
                            )
                        }

                        // 2. Perfil del Usuario Logueado
                        composable("profile") {
                            ProfileScreen(
                                onBack = { navController.popBackStack() },
                                onLogoutSuccess = {
                                    navController.navigate("catalog") {
                                        popUpTo(0) // Limpia el historial para que no pueda volver atrás
                                    }
                                }
                            )
                        }

                        // 3. Lista de Contratistas por Categoría (Pública)
                        composable(
                            route = "contractors/{categoryId}",
                            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val catId = backStackEntry.arguments?.getInt("categoryId") ?: 0
                            ContractorListScreen(
                                categoryId = catId,
                                onBack = { navController.popBackStack() },
                                onContractorClick = { id -> navController.navigate("contractor_detail/$id") }
                            )
                        }

                        // 4. Detalle del Profesional + Reseñas (Pública/Privada)
                        composable(
                            route = "contractor_detail/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id") ?: 0
                            ContractorDetailScreen(
                                contractorId = id,
                                onBack = { navController.popBackStack() },
                                onContactClick = { recId, recName ->
                                    navController.navigate("chat/$recId/$recName")
                                }
                            )
                        }

                        // 5. Bandeja de Entrada / Mensajes (Privada)
                        composable("messages") {
                            MessagesScreen(
                                onChatClick = { id, name ->
                                    navController.navigate("chat/$id/$name")
                                }
                            )
                        }

                        // 6. Pantalla de Chat en Tiempo Real (Privada)
                        composable(
                            route = "chat/{receiverId}/{receiverName}",
                            arguments = listOf(
                                navArgument("receiverId") { type = NavType.IntType },
                                navArgument("receiverName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("receiverId") ?: 0
                            val name = backStackEntry.arguments?.getString("receiverName") ?: "Usuario"
                            ChatScreen(receiverId = id, receiverName = name, onBack = { navController.popBackStack() })
                        }

                        // 7. Pantalla de Login
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("catalog") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { navController.navigate("register") }
                            )
                        }

                        // 8. Pantalla de Registro
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}