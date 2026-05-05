package com.example.myfoodappfinal.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myfoodappfinal.data.repository.FoodRepository
import com.example.myfoodappfinal.security.MyBiometricManager
import com.example.myfoodappfinal.ui.*

@Composable
fun AppNavigation(
    repository: FoodRepository,
    biometricManager: MyBiometricManager,
    router: DeepLinkRouter
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Стан Deep Link з роутера
    val deepLinkState by router.currentDestination.collectAsState()

    // Стан списку продуктів
    val foodListState = repository.getFoodsStream().collectAsState(initial = emptyList())
    val foodList = foodListState.value

    // --- ЛОГІКА ОБРОБКИ DEEP LINKS ---
    LaunchedEffect(deepLinkState) {
        deepLinkState?.let { dest ->
            when (dest) {
                is Destination.Home -> navController.navigate("list") {
                    popUpTo(0) // Очищуємо стек для повернення на головну
                }
                is Destination.Detail -> navController.navigate("details/${dest.id}")
                is Destination.Catalog -> navController.navigate("list")
                is Destination.Invite -> navController.navigate("invite/${dest.token}")
                is Destination.Debug -> navController.navigate("debug")
                is Destination.Notifications -> navController.navigate("profile") // Як приклад
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                // 1. Меню
                NavigationBarItem(
                    selected = currentRoute == "list" || currentRoute?.startsWith("details") == true,
                    label = { Text("Меню") },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    onClick = {
                        navController.navigate("list") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // 2. Кошик
                NavigationBarItem(
                    selected = currentRoute == "cart",
                    label = { Text("Кошик") },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    onClick = {
                        navController.navigate("cart") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // 3. Профіль
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    label = { Text("Профіль") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // 4. Сервіс/Налаштування
                NavigationBarItem(
                    selected = currentRoute == "settings" || currentRoute == "security_settings",
                    label = { Text("Сервіс") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = {
                        navController.navigate("settings") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "list",
            modifier = Modifier.padding(innerPadding)
        ) {
            // ГОЛОВНИЙ СПИСОК
            composable("list") {
                FoodListScreen(
                    foods = foodList,
                    onItemClick = { id -> navController.navigate("details/$id") },
                    onAddToCart = { food -> repository.updateQuantity(food, 1) } // Додаємо 1 штуку
                )
            }

            // ПРОФІЛЬ
            composable("profile") {
                ProfileScreen(repository = repository)
            }

            // КОШИК
            composable("cart") {
                CartScreen(repository = repository)
            }
            // НАЛАШТУВАННЯ
            composable("settings") {
                SettingsScreen(onNavigateToSecurity = {
                    navController.navigate("security_settings")
                })
            }

            // БІОМЕТРІЯ
            composable("security_settings") {
                SecuritySettingsScreen(manager = biometricManager)
            }

            // ДЕТАЛІ СТРАВИ
            composable(
                "details/{foodId}",
                arguments = listOf(navArgument("foodId") { type = NavType.IntType })
            ) { backStackEntry ->
                val foodId = backStackEntry.arguments?.getInt("foodId")
                val selectedFood = foodList.find { it.id == foodId }
                FoodDetailScreen(food = selectedFood, onBack = {
                    navController.popBackStack()
                })
            }

            // --- НОВІ МАРШРУТИ ДЛЯ DEEP LINKS ---

            // ЕКРАН ЗАПРОШЕННЯ
            composable("invite/{token}") { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                InviteScreen(token = token, onAccept = {
                    navController.popBackStack()
                })
            }

            // DEBUG ЕКРАН
            composable("debug") {
                DebugScreen(router = router)
            }
        }
    }
}