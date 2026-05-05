package com.example.myfoodappfinal

import com.example.myfoodappfinal.model.Food
import com.example.myfoodappfinal.data.remote.SocketState
import com.example.myfoodappfinal.data.remote.SocketManager
import com.example.myfoodappfinal.model.MockData
import org.junit.Assert.*
import org.junit.Test

class LogicTests {

    // --- ТЕСТИ МОДЕЛІ (5) ---
    @Test
    fun testFoodDefaultSyncStatus() {
        val food = Food(title = "Test", price = 10.0, description = "", rating = 5.0)
        assertEquals("synced", food.syncStatus)
    }

    @Test
    fun testFoodPriceStorage() {
        val food = Food(title = "Pizza", price = 250.5, description = "Hot", rating = 4.0)
        assertEquals(250.5, food.price, 0.0)
    }

    @Test
    fun testFoodCopyLogic() {
        val food = Food(title = "Burger", price = 100.0, description = "Meat", rating = 4.0)
        val updated = food.copy(price = 120.0)
        assertEquals(120.0, updated.price, 0.0)
        assertEquals("Burger", updated.title)
    }

    @Test
    fun testCategoryData() {
        val cat = com.example.myfoodappfinal.model.Category(1, "Burgers", "img", true)
        assertTrue(cat.isActive)
    }

    @Test
    fun testMockItemsNotEmpty() {
        assertTrue(MockData.items.isNotEmpty())
    }

    // --- ТЕСТИ SOCKET MANAGER (5) ---
    @Test
    fun testInitialSocketState() {
        val sm = SocketManager()
        assertEquals(SocketState.Disconnected, sm.state.value)
    }

    @Test
    fun testSocketDisconnect() {
        val sm = SocketManager()
        sm.disconnect()
        assertEquals(SocketState.Disconnected, sm.state.value)
    }

    @Test
    fun testSocketMessagesInitialNull() {
        val sm = SocketManager()
        assertNull(sm.messages.value)
    }

    @Test
    fun testMockCategoriesCount() {
        assertEquals(3, MockData.categories.size)
    }

    @Test
    fun testMockDataFirstItemTitle() {
        assertEquals("Чізбургер", MockData.items[0].title)
    }

    // --- ТЕСТИ БІЗНЕС-ЛОГІКИ (5) ---
    @Test
    fun testPriceCalculation() {
        val sum = MockData.items.sumOf { it.price }
        assertTrue(sum > 0)
    }

    @Test
    fun testRatingRange() {
        val food = Food(title = "A", price = 1.0, description = "", rating = 4.9)
        assertTrue(food.rating in 0.0..5.0)
    }

    @Test
    fun testSyncStatusUpdate() {
        val food = Food(title = "A", price = 1.0, description = "", rating = 4.5)
        val pending = food.copy(syncStatus = "pending")
        assertEquals("pending", pending.syncStatus)
    }

    @Test
    fun testIdAutoIncrementSim() {
        val food = Food(id = 10, title = "A", price = 1.0, description = "", rating = 1.0)
        assertEquals(10, food.id)
    }

    @Test
    fun testSearchLogic() {
        val query = "Піца"
        val found = MockData.items.any { it.title.contains(query) }
        assertTrue(found)
    }
}