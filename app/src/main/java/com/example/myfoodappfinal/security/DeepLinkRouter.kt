package com.example.myfoodappfinal.navigation

sealed class Destination {
    object Home : Destination()
    data class Detail(val id: Int) : Destination()
    data class Catalog(val filter: String?) : Destination()
    data class Invite(val token: String) : Destination()
    object Notifications : Destination()
    object Debug : Destination() // Для тестування
}