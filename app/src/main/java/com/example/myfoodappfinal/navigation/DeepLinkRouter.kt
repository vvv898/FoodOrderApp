package com.example.myfoodappfinal.navigation

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeepLinkRouter {
    private val _currentDestination = MutableStateFlow<Destination?>(null)
    val currentDestination = _currentDestination.asStateFlow()

    private val customScheme = "foodapp://"
    private val webHost = "https://myfoodapp.com/"

    fun parseURL(url: String): Destination? {
        if (url.isBlank()) return null
        val uri = Uri.parse(url)

        // Перевіряємо схему (foodapp:// або https://myfoodapp.com/)
        val isCustom = url.startsWith(customScheme)
        val isWeb = url.startsWith(webHost)
        if (!isCustom && !isWeb) return null

        val path = uri.path?.removePrefix("/") ?: ""
        val host = uri.host ?: ""

        return when {
            // foodapp://home
            host == "home" || path == "home" -> Destination.Home

            // foodapp://items/123 або https://myfoodapp.com/items/123
            host == "items" || path.startsWith("items") -> {
                val id = uri.lastPathSegment?.toIntOrNull()
                if (id != null) Destination.Detail(id) else null
            }

            // foodapp://catalog?filter=pizza
            host == "catalog" || path == "catalog" -> {
                val filter = uri.getQueryParameter("filter")
                Destination.Catalog(filter)
            }

            // foodapp://invite/TOKEN_ABC
            host == "invite" || path.startsWith("invite") -> {
                val token = uri.lastPathSegment
                if (token != null) Destination.Invite(token) else null
            }

            host == "notifications" || path == "notifications" -> Destination.Notifications
            host == "debug" -> Destination.Debug

            else -> null
        }
    }

    fun handle(url: String) {
        val destination = parseURL(url)
        if (destination != null) {
            navigate(destination)
        } else {
            println("DeepLinkRouter: Unknown URL: $url")
        }
    }

    fun navigate(destination: Destination) {
        _currentDestination.value = destination
    }
}