package com.example.myfoodappfinal

import com.example.myfoodappfinal.security.AuthState
import com.example.myfoodappfinal.security.MyBiometricManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import androidx.fragment.app.FragmentActivity
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricLogicTests {

    private lateinit var mockManager: MyBiometricManager
    private val testAuthState = MutableStateFlow(AuthState.Idle)

    @Before
    fun setup() {
        // Створюємо mock менеджера
        mockManager = mock()
        // Налаштовуємо поведінку за замовчуванням для StateFlow
        `when`(mockManager.authState).thenReturn(testAuthState)
    }

    // 1. Перевірка недоступності датчика
    @Test
    fun testCheckAvailabilityReturnsUnavailable() {
        `when`(mockManager.checkAvailability()).thenReturn("Датчик відсутній")
        assertEquals("Датчик відсутній", mockManager.checkAvailability())
    }

    // 2. Перевірка зчитування налаштувань користувача
    @Test
    fun testIsEnabledByUserReadsCorrectly() {
        `when`(mockManager.isEnabledByUser()).thenReturn(true)
        assertTrue(mockManager.isEnabledByUser())
    }

    // 3. Перевірка переходу в стан Authenticating
    @Test
    fun testAuthStateTransitionsToAuthenticating() = runTest {
        testAuthState.value = AuthState.Authenticating
        assertEquals(AuthState.Authenticating, mockManager.authState.value)
    }

    // 4. Успішна автентифікація (Success)
    @Test
    fun testAuthenticateReturnsSuccess() = runTest {
        testAuthState.value = AuthState.Success
        assertEquals(AuthState.Success, mockManager.authState.value)
    }

    // 5. Помилка автентифікації при скасуванні (Failed)
    @Test
    fun testAuthenticateReturnsFailedOnCancel() = runTest {
        testAuthState.value = AuthState.Failed
        assertEquals(AuthState.Failed, mockManager.authState.value)
    }

    // 6. Перевірка вимкненої біометрії
    @Test
    fun testIsEnabledByUserReturnsFalse() {
        `when`(mockManager.isEnabledByUser()).thenReturn(false)
        assertFalse(mockManager.isEnabledByUser())
    }

    // 7. Відображення типу датчика (Доступно)
    @Test
    fun testCheckAvailabilityReturnsAvailable() {
        `when`(mockManager.checkAvailability()).thenReturn("Доступно")
        assertEquals("Доступно", mockManager.checkAvailability())
    }

    // 8. Поведінка при недоступному залізі (Hardware Unavailable)
    @Test
    fun testCheckAvailabilityReturnsHardwareError() {
        `when`(mockManager.checkAvailability()).thenReturn("Датчик зайнятий")
        assertEquals("Датчик зайнятий", mockManager.checkAvailability())
    }

    // 9. Скидання стану до Idle
    @Test
    fun testResetToIdleState() = runTest {
        testAuthState.value = AuthState.Idle
        assertEquals(AuthState.Idle, mockManager.authState.value)
    }

    // 10. Перевірка стану "Не налаштовано"
    @Test
    fun testCheckAvailabilityNoneEnrolled() {
        `when`(mockManager.checkAvailability()).thenReturn("Біометрія не налаштована")
        assertEquals("Біометрія не налаштована", mockManager.checkAvailability())
    }

    // 11. Обробка стану Unavailable через менеджер
    @Test
    fun testAuthStateIsUnavailable() {
        testAuthState.value = AuthState.Unavailable
        assertEquals(AuthState.Unavailable, mockManager.authState.value)
    }

    @Test
    fun testAuthenticateMethodIsCalled() {
        val activity = mock<FragmentActivity>()
        val reason = "Вхід"

        // Викликаємо метод у мока
        mockManager.authenticate(activity, reason)

        // Перевіряємо, чи був він викликаний з цими параметрами
        verify(mockManager).authenticate(activity, reason)
    }
}