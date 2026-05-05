package com.example.myfoodappfinal

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.myfoodappfinal.data.local.AppDatabase
import com.example.myfoodappfinal.data.remote.FoodApi
import com.example.myfoodappfinal.data.remote.SocketManager
import com.example.myfoodappfinal.data.repository.FoodRepository
import com.example.myfoodappfinal.navigation.AppNavigation
import com.example.myfoodappfinal.navigation.DeepLinkRouter
import com.example.myfoodappfinal.security.MyBiometricManager
import com.example.myfoodappfinal.ui.theme.FoodOrderAppTheme
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    // Оголошуємо роутер на рівні класу, щоб він був доступний в onNewIntent
    private lateinit var router: DeepLinkRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Ініціалізація роутера для Deep Links
        router = DeepLinkRouter()

        // 2. Ініціалізація бази даних Room
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "food_db"
        ).fallbackToDestructiveMigration().build()

        // 3. Ініціалізація мережевих компонентів та репозиторію
        val socketManager = SocketManager()
        val repository = FoodRepository(db.foodDao(), FoodApi(), socketManager)

        // 4. Ініціалізація менеджера біометрії
        val biometricManager = MyBiometricManager(this)

        // 5. Обробка Deep Link при "Холодному старті" (додаток був закритий)
        intent?.data?.let { uri ->
            router.handle(uri.toString())
        }

        // 6. Логіка запуску (завантаження даних + біометрія)
        lifecycleScope.launch {
            // Завантажуємо початкові дані
            repository.getFoods()

            // Перевіряємо біометрію, якщо увімкнено
            if (biometricManager.isEnabledByUser()) {
                biometricManager.authenticate(
                    activity = this@MainActivity,
                    reason = "Увійдіть у систему FoodApp"
                )
            }
        }

        // 7. Активація Real-time оновлень
        repository.observeSocketUpdates(lifecycleScope)
        socketManager.connect("wss://echo.websocket.org")

        // 8. Відображення інтерфейсу
        setContent {
            FoodOrderAppTheme {
                AppNavigation(
                    repository = repository,
                    biometricManager = biometricManager,
                    router = router // Передаємо роутер у навігацію
                )
            }
        }
    }

    // 9. Обробка Deep Link при "Теплому старті" (додаток у фоні)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Оновлюємо intent активності
        intent.data?.let { uri ->
            router.handle(uri.toString())
        }
    }
}