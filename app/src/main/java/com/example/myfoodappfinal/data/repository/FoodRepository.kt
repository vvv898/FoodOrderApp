package com.example.myfoodappfinal.data.repository

import com.example.myfoodappfinal.data.local.FoodDao
import com.example.myfoodappfinal.data.remote.FoodApi
import com.example.myfoodappfinal.data.remote.SocketManager
import com.example.myfoodappfinal.model.CartItem
import com.example.myfoodappfinal.model.Food
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
СТРАТЕГІЯ: OFFLINE-FIRST + REAL-TIME + CART MANAGEMENT
*/

class FoodRepository(
    private val dao: FoodDao,
    private val api: FoodApi,
    private val socketManager: SocketManager
) {

    // --- ЛОГІКА КОШИКА ---

    // Стан кошика: Map, де ключ - ID страви, значення - CartItem
    private val _cartItems = MutableStateFlow<Map<Int, CartItem>>(emptyMap())
    val cartItems = _cartItems.asStateFlow()

    // Додати/змінити кількість (від 0 до 20)
    fun updateQuantity(food: Food, newQuantity: Int) {
        val currentMap = _cartItems.value.toMutableMap()

        if (newQuantity <= 0) {
            currentMap.remove(food.id)
        } else {
            // Обмежуємо кількість максимум 20 одиницями
            val clampedQuantity = newQuantity.coerceAtMost(20)
            currentMap[food.id] = CartItem(food, clampedQuantity)
        }
        _cartItems.value = currentMap
    }

    // Отримати загальну суму кошика
    fun getTotalPrice(): Double {
        return _cartItems.value.values.sumOf { it.food.price * it.quantity }
    }

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    // Імітація збережених даних користувача
    var userName = MutableStateFlow("")
    var userPhone = MutableStateFlow("")
    var userEmail = MutableStateFlow("")

    fun register(name: String, phone: String, email: String) {
        userName.value = name
        userPhone.value = phone
        userEmail.value = email
        _isLoggedIn.value = true
    }

    fun login() {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
        _cartItems.value = emptyMap() // Очищуємо кошик при виході
    }

    // --- ЛОГІКА ДАНИХ (ROOM & API) ---

    // Читаємо з БД (Offline-first)
    suspend fun getFoods(): List<Food> {
        val local = dao.getAll()
        if (local.isNotEmpty()) return local

        val remote = api.getFoods()
        dao.insertAll(remote)
        return remote
    }

    // Примусове оновлення даних з сервера
    suspend fun refreshFoods() {
        val remote = api.getFoods()
        dao.insertAll(remote.map { it.copy(syncStatus = "synced") })
    }

    suspend fun getFood(id: Int): Food? {
        return dao.getById(id)
    }

    suspend fun addFood(food: Food) {
        dao.insert(food.copy(syncStatus = "pending"))
    }

    suspend fun deleteFood(food: Food) {
        dao.delete(food)
    }

    // --- REAL-TIME ОНОВЛЕННЯ (WEBSOCKET) ---

    fun observeSocketUpdates(scope: CoroutineScope) {
        scope.launch {
            socketManager.messages.collect { json ->
                json?.let {
                    // Коли приходить подія PRICE_UPDATE
                    if (it.contains("PRICE_UPDATE")) {
                        val id = it.substringAfter("\"id\":").substringBefore(",").trim().toInt()
                        val newPrice = it.substringAfter("\"newPrice\":").substringBefore("}").trim().toDouble()

                        // Автоматично оновлюємо базу Room
                        val food = dao.getById(id)
                        food?.let { f ->
                            dao.insert(f.copy(price = newPrice))
                        }
                    }
                }
            }
        }
    }

    // Стрім даних для UI
    fun getFoodsStream(): kotlinx.coroutines.flow.Flow<List<Food>> {
        return dao.getAllFlow()
    }
}