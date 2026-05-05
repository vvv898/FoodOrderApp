package com.example.myfoodappfinal.ui

import androidx.compose.foundation.layout.* // Для Column, fillMaxSize, padding, Arrangement
import androidx.compose.material3.*        // Для Text, Button, MaterialTheme
import androidx.compose.runtime.Composable // Для анотації @Composable
import androidx.compose.ui.Modifier       // Для модифікаторів
import androidx.compose.ui.unit.dp        // Для розмірів у dp

@Composable
fun InviteScreen(token: String, onAccept: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        Text("Вас запрошено!", style = MaterialTheme.typography.headlineMedium)
        Text("Токен запрошення: $token")
        Button(onClick = onAccept) { Text("Прийняти") }
    }
}