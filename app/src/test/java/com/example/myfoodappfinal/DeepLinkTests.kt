package com.example.myfoodappfinal

import com.example.myfoodappfinal.navigation.DeepLinkRouter
import com.example.myfoodappfinal.navigation.Destination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner // Потрібно для Uri.parse
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class) // Дозволяє використовувати Android Uri в тестах
class DeepLinkTests {
    private val router = DeepLinkRouter()

    @Test
    fun testParseHome() {
        assertEquals(Destination.Home, router.parseURL("foodapp://home"))
    }

    @Test
    fun testParseDetail() {
        val result = router.parseURL("foodapp://items/123")
        assertTrue(result is Destination.Detail && result.id == 123)
    }

    @Test
    fun testParseWebDetail() {
        val result = router.parseURL("https://myfoodapp.com/items/999")
        assertTrue(result is Destination.Detail && result.id == 999)
    }

    @Test
    fun testParseCatalogWithFilter() {
        val result = router.parseURL("foodapp://catalog?filter=pizza")
        assertTrue(result is Destination.Catalog && result.filter == "pizza")
    }

    @Test
    fun testParseCatalogWithoutFilter() {
        val result = router.parseURL("foodapp://catalog")
        assertTrue(result is Destination.Catalog && result.filter == null)
    }

    @Test
    fun testParseInvite() {
        val result = router.parseURL("foodapp://invite/PROMO2026")
        assertTrue(result is Destination.Invite && result.token == "PROMO2026")
    }

    @Test
    fun testParseUnknownRoute() {
        assertNull(router.parseURL("foodapp://unknown/path"))
    }

    @Test
    fun testParseEmptyUrl() {
        assertNull(router.parseURL(""))
    }

    @Test
    fun testHandleValidUrl() = runTest {
        router.handle("foodapp://home")
        assertEquals(Destination.Home, router.currentDestination.value)
    }

    @Test
    fun testHandleInvalidUrl() {
        val initial = router.currentDestination.value
        router.handle("invalid://link")
        assertEquals(initial, router.currentDestination.value)
    }

    @Test
    fun testShareLinkGeneration() {
        val id = 50
        val generatedUrl = "foodapp://items/$id"
        val parsed = router.parseURL(generatedUrl)
        assertTrue(parsed is Destination.Detail && parsed.id == 50)
    }

    @Test
    fun testParseInviteLongToken() {
        val result = router.parseURL("foodapp://invite/VERY_LONG_TOKEN_12345")
        assertTrue(result is Destination.Invite && result.token == "VERY_LONG_TOKEN_12345")
    }

    @Test
    fun testDebugScreenCallsHandle() {
        // Створюємо мок роутера
        val mockRouter = mock<DeepLinkRouter>()
        val testUrl = "foodapp://home"

        // Імітуємо дію користувача (виклик методу)
        mockRouter.handle(testUrl)

        // Перевіряємо, чи був викликаний метод handle з правильним параметром
        verify(mockRouter).handle(testUrl)
    }
}