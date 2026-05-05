package com.example.myfoodappfinal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.myfoodappfinal.security.AuthState
import com.example.myfoodappfinal.security.MyBiometricManager

@Composable
fun SecuritySettingsScreen(manager: MyBiometricManager) {
    // Отримуємо контекст як FragmentActivity для роботи BiometricPrompt
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var isEnabled by remember { mutableStateOf(manager.isEnabledByUser()) }
    val availability = manager.checkAvailability()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Налаштування безпеки", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Блок 1: Увімкнення біометрії
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Біометричний вхід", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Статус: $availability",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (availability == "Доступно") Color.DarkGray else Color.Red
                    )
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = {
                        isEnabled = it
                        manager.setEnabledByUser(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(24.dp))

        // Блок 2: Небезпечна зона (Критична дія)
        Text("Критичні дії", style = MaterialTheme.typography.titleLarge, color = Color.Red)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Ці дії потребують обов'язкового підтвердження відбитком пальця.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Виклик компонента підтвердження
        activity?.let {
            CriticalActionScreen(
                activity = it,
                manager = manager,
                onActionConfirmed = {
                    // ТУТ ЛОГІКА ВИДАЛЕННЯ (наприклад, очищення списку або SharedPreferences)
                    println("ДІЯ ПІДТВЕРДЖЕНА: Дані успішно очищено!")
                }
            )
        }
    }
}

@Composable
fun CriticalActionScreen(
    activity: FragmentActivity,
    manager: MyBiometricManager,
    onActionConfirmed: () -> Unit
) {
    val state by manager.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Використовуємо LaunchedEffect, щоб дія спрацювала лише ОДИН РАЗ при успіху
    LaunchedEffect(state) {
        if (state == AuthState.Success) {
            onActionConfirmed()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { manager.authenticate(activity, "Підтвердіть видалення всіх даних") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Видалити всі дані застосунку", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Відображення поточного стану процесу
        when (state) {
            AuthState.Authenticating -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Очікування відбитка...")
                }
            }
            AuthState.Failed -> {
                Text("Помилка або скасовано. Спробуйте ще раз.", color = Color.Red, style = MaterialTheme.typography.labelSmall)
            }
            AuthState.Success -> {
                Text("Підтверджено успішно", color = Color(0xFF388E3C))
            }
            else -> {}
        }
    }
}