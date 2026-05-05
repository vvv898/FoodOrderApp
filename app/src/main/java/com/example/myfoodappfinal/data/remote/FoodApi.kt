package com.example.myfoodappfinal.data.remote

import com.example.myfoodappfinal.model.Food
import com.example.myfoodappfinal.model.MockData
import kotlinx.coroutines.delay

/*
API CONTRACT (REST)

GET /foods -> список
GET /foods/{id} -> один
POST /foods -> створення
*/

class FoodApi {

    suspend fun getFoods(): List<Food> {
        delay(1000)
        return MockData.items
    }

    suspend fun getFood(id: Int): Food? {
        delay(500)
        // Шукай за id, а не за назвою
        return MockData.items.find { it.id == id }
    }

    suspend fun addFood(food: Food): Food {
        delay(500)
        return food
    }
}