package com.example.myfoodappfinal.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Стани автентифікації згідно з завданням
enum class AuthState { Idle, Authenticating, Success, Failed, Unavailable }

class MyBiometricManager(private val context: Context) {

    private val biometricManager = BiometricManager.from(context)

    private val _authState = MutableStateFlow(AuthState.Idle)
    val authState = _authState.asStateFlow()

    // Перевірка наявності датчика
    fun checkAvailability(): String {
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> "Доступно"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "Датчик відсутній"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Датчик зайнятий"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "Біометрія не налаштована"
            else -> "Недоступно"
        }
    }

    // Запит автентифікації
    fun authenticate(activity: FragmentActivity, reason: String) {
        _authState.value = AuthState.Authenticating

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    _authState.value = AuthState.Success
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    _authState.value = AuthState.Failed
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    _authState.value = AuthState.Failed
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Вхід у систему")
            .setSubtitle(reason)
            .setNegativeButtonText("Використати пароль")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // Збереження вибору в SharedPreferences
    fun setEnabledByUser(enabled: Boolean) {
        val prefs = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    fun isEnabledByUser(): Boolean {
        val prefs = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("biometric_enabled", false)
    }
}