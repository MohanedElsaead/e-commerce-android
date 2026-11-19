package com.example.route.data.repository

import com.example.route.data.local.AuthPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesRepository(
    private val prefs: AuthPreferences,
    private val scope: CoroutineScope,
) {
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        scope.launch {
            prefs.favoritesFlow.collect { ids ->
                _favoriteIds.value = ids
            }
        }
    }

    fun toggleFavorite(productId: String) {
        val current = _favoriteIds.value
        val updated = if (productId in current) current - productId else current + productId
        _favoriteIds.value = updated
        scope.launch { prefs.saveFavorites(updated) }
    }

    fun isFavorite(productId: String): Boolean = productId in _favoriteIds.value
}
