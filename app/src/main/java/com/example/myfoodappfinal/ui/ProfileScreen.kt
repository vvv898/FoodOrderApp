package com.example.myfoodappfinal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myfoodappfinal.data.repository.FoodRepository

@Composable
fun ProfileScreen(repository: FoodRepository) {
    val isLoggedIn by repository.isLoggedIn.collectAsState()

    // Стан для перемикання між Входом та Реєстрацією всередині екрана
    var isRegisterMode by remember { mutableStateOf(false) }

    // Поля введення
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoggedIn) {
            // --- ЕКРАН ПРОФІЛЮ АВТОРИЗОВАНОГО КОРИСТУВАЧА ---
            Text("Вітаємо, ${repository.userName.collectAsState().value}!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Пошта: ${repository.userEmail.collectAsState().value}")
            Text("Тел: ${repository.userPhone.collectAsState().value}")

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { repository.logout() }, modifier = Modifier.fillMaxWidth()) {
                Text("Вийти")
            }
        } else {
            // --- ФОРМА ВХОДУ / РЕЄСТРАЦІЇ ---
            Text(
                text = if (isRegisterMode) "Реєстрація" else "Вхід",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isRegisterMode) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Ім'я") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Номер телефону") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isRegisterMode) {
                        repository.register(name, phone, email)
                    } else {
                        repository.login()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRegisterMode) "Створити акаунт" else "Увійти")
            }

            Spacer(modifier = Modifier.height(8.dp))

            val context = LocalContext.current
            val activity = context as? FragmentActivity

            activity?.let {
                OutlinedButton(
                    onClick = {
                        val manager = MyBiometricManager(context)
                        manager.authenticate(it, "Увійдіть через біометрію")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Увійти через біометрію")
                }
            }

            TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                Text(if (isRegisterMode) "Вже є акаунт? Увійти" else "Немає акаунту? Реєстрація")
            }
        }
    }
}