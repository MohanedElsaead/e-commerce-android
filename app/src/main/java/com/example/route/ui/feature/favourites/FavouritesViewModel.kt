package com.example.route.ui.feature.favourites

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.route.data.local.AuthPreferences
import com.example.route.data.repository.FavoritesRepository
import com.example.route.data.repository.LocalStoreRepository
import com.example.route.data.repository.StoreRepository
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val storeRepository: StoreRepository = LocalStoreRepository(),
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FavouritesState())
    val state: StateFlow<FavouritesState> = _state.asStateFlow()

    init {
        observeFavorites()
        onIntent(FavouritesIntent.LoadFavourites)
    }

    fun onIntent(intent: FavouritesIntent) {
        when (intent) {
            FavouritesIntent.LoadFavourites -> loadProducts()
            is FavouritesIntent.ToggleFavorite -> {
                favoritesRepository.toggleFavorite(intent.productId)
            }
            is FavouritesIntent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.favoriteIds.collect { ids ->
                _state.update { it.copy(favoriteIds = ids) }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                storeRepository.getProducts()
            }.onSuccess { products ->
                _state.update { it.copy(isLoading = false, allProducts = products) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val prefs = AuthPreferences(context.applicationContext)
                    val scope = kotlinx.coroutines.CoroutineScope(
                        SupervisorJob() + kotlinx.coroutines.Dispatchers.Main.immediate
                    )
                    val favoritesRepo = FavoritesRepository(prefs, scope)
                    return FavouritesViewModel(favoritesRepository = favoritesRepo) as T
                }
            }
    }
}
