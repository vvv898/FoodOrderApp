package com.example.myfoodappfinal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfoodappfinal.data.repository.FoodRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(repository: FoodRepository) {
    // Підписка на стани з репозиторію
    val cartItems by repository.cartItems.collectAsState()
    val isLoggedIn by repository.isLoggedIn.collectAsState()

    val itemsList = cartItems.values.toList()
    val totalPrice = repository.getTotalPrice()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ваш кошик") }) },
        bottomBar = {
            // Показуємо нижню панель тільки якщо в кошику є товари
            if (itemsList.isNotEmpty()) {
                Surface(tonalElevation = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Рядок з підсумковою ціною
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Разом:", style = MaterialTheme.typography.headlineSmall)
                            Text(
                                text = "$totalPrice грн",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Логіка відображення кнопки залежно від статусу входу
                        if (isLoggedIn) {
                            Button(
                                onClick = {
                                    /* Тут могла б бути логіка відправки замовлення на сервер */
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ОФОРМИТИ ЗАМОВЛЕННЯ")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { repository.login() }, // Імітація входу
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("УВІЙТИ, ЩОБ ЗАМОВИТИ")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (itemsList.isEmpty()) {
            // Порожній стан
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Кошик порожній", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            // Список товарів у кошику
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(itemsList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.food.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "${item.food.price * item.quantity} грн",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Керування кількістю (від 0 до 20)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { repository.updateQuantity(item.food, item.quantity - 1) }
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Менше")
                                }

                                Text(
                                    text = "${item.quantity}",
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                IconButton(
                                    onClick = { repository.updateQuantity(item.food, item.quantity + 1) }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Більше")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}