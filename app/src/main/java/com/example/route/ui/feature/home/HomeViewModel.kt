package com.example.route.ui.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.route.data.local.AuthPreferences
import com.example.route.data.repository.FavoritesRepository
import com.example.route.data.repository.LocalStoreRepository
import com.example.route.data.repository.StoreRepository
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val storeRepository: StoreRepository = LocalStoreRepository(),
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeFavorites()
        onIntent(HomeIntent.LoadHome)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHome -> loadHomeData()

            is HomeIntent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            is HomeIntent.ToggleFavorite -> {
                favoritesRepository.toggleFavorite(intent.productId)
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                coroutineScope {
                    val categoriesDeferred = async { storeRepository.getCategories() }
                    val productsDeferred = async { storeRepository.getProducts() }
                    categoriesDeferred.await() to productsDeferred.await()
                }
            }.onSuccess { (categories, products) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        categories = categories,
                        products = products
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
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
                    return HomeViewModel(favoritesRepository = favoritesRepo) as T
                }
            }
    }
}
