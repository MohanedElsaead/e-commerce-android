package com.example.route.ui.feature.profile

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false,
)

sealed interface ProfileIntent {
    data object Logout : ProfileIntent
}
