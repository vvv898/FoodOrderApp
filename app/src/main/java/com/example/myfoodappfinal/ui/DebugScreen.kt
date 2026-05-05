package com.example.myfoodappfinal.ui

import androidx.compose.foundation.layout.* // Для Column, padding
import androidx.compose.material3.*        // Для TextField, Button, Text
import androidx.compose.runtime.*          // Для remember, mutableStateOf, getValue, setValue
import androidx.compose.ui.Modifier       // Для модифікаторів
import androidx.compose.ui.unit.dp        // Для розмірів у dp
import com.example.myfoodappfinal.navigation.DeepLinkRouter // Твій роутер

@Composable
fun DebugScreen(router: DeepLinkRouter) {
    var urlText by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = urlText, onValueChange = { urlText = it }, label = { Text("Введіть Deep Link") })
        Button(onClick = { router.handle(urlText) }) { Text("Тестувати URL") }
    }
}