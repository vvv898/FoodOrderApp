package com.example.myfoodappfinal.data.remote

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

enum class SocketState { Disconnected, Connecting, Connected, Reconnecting }

class SocketManager {
    private val _state = MutableStateFlow(SocketState.Disconnected)
    val state = _state.asStateFlow()

    private val _messages = MutableStateFlow<String?>(null)
    val messages = _messages.asStateFlow()

    private var socketJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun connect(url: String) {
        if (_state.value == SocketState.Connected) return

        socketJob?.cancel()
        socketJob = scope.launch {
            _state.value = SocketState.Connecting
            delay(1000) // Імітація встановлення зв'язку
            _state.value = SocketState.Connected

            // Імітація вхідних повідомлень (наприклад, зміна ціни або нове замовлення)
            while (isActive) {
                delay(5000) // Кожні 5 секунд приходить подія
                val mockJson = """{"type":"PRICE_UPDATE", "id":${Random.nextInt(1, 4)}, "newPrice":${Random.nextInt(100, 300)}}"""
                _messages.value = mockJson
            }
        }
    }

    fun disconnect() {
        socketJob?.cancel()
        _state.value = SocketState.Disconnected
    }

    fun send(message: String) {
        // В реальності тут був би socket.send(message)
        println("WS SENT: $message")
    }
}