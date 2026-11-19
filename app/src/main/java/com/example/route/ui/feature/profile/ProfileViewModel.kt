package com.example.route.ui.feature.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.route.data.local.AuthPreferences
import com.example.route.data.repository.AuthRepository
import com.example.route.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeUserSession()
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Logout -> logout()
        }
    }

    private fun observeUserSession() {
        viewModelScope.launch {
            combine(
                authRepository.getUserName(),
                authRepository.getUserEmail()
            ) { name, email ->
                name.orEmpty() to email.orEmpty()
            }.collect { (name, email) ->
                _state.update { it.copy(userName = name, userEmail = email) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true) }
            authRepository.clearSession()
            _state.update { it.copy(isLoggingOut = false, isLoggedOut = true) }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo = AuthRepositoryImpl(AuthPreferences(context.applicationContext))
                    return ProfileViewModel(repo) as T
                }
            }
    }
}
