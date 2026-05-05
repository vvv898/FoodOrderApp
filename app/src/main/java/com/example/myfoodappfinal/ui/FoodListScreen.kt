package com.example.myfoodappfinal.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfoodappfinal.model.Food

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen(
    foods: List<Food>,
    onItemClick: (Int) -> Unit,
    onAddToCart: (Food) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Меню") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(foods) { food ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onItemClick(food.id) }
                ) {
                    // Використовуємо Row, щоб кнопка була праворуч від тексту
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = food.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "${food.price} грн",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Додаткова інформація про синхронізацію
                            if (food.syncStatus == "pending") {
                                Text(
                                    text = "Очікує синхронізації...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        // Кнопка додавання в кошик
                        Button(onClick = { onAddToCart(food) }) {
                            Text("В кошик")
                        }
                    }
                }
            }
        }
    }
}