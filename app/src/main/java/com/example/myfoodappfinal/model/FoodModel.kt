package com.example.myfoodappfinal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Сутність "Страва" */
@Entity(tableName = "food_table")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ЗАЛИШ ТІЛЬКИ ОДИН РАЗ

    val title: String,
    val price: Double,
    val description: String,
    val rating: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "synced"
)

/** Сутність "Категорія" */
data class Category(
    val id: Int,
    val title: String,
    val imageRes: String,
    val isActive: Boolean
)

data class CartItem(
    val food: Food,
    val quantity: Int
)

/** Фіктивні дані */
object MockData {

    val categories = listOf(
        Category(1, "Бургери", "burger", true),
        Category(2, "Піца", "pizza", true),
        Category(3, "Напої", "drink", true)
    )

    val items = listOf(
        // Додаємо жорсткі ID, щоб Room та WebSocket розуміли, кого оновлювати
        Food(id = 1, title = "Чізбургер", price = 150.0, description = "Соковита яловичина", rating = 4.8),
        Food(id = 2, title = "Піца", price = 220.0, description = "Моцарела", rating = 4.9),
        Food(id = 3, title = "Кола", price = 45.0, description = "Напій", rating = 4.5)
    )
}