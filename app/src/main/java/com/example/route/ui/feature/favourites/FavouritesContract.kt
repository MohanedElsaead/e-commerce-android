package com.example.route.ui.feature.favourites

import com.example.route.data.remote.dto.ProductDto

data class FavouritesState(
    val isLoading: Boolean = false,
    val allProducts: List<ProductDto> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val searchQuery: String = "",
) {
    val favouriteProducts: List<ProductDto>
        get() {
            val favorites = allProducts.filter { it.id in favoriteIds }
            return if (searchQuery.isBlank()) {
                favorites
            } else {
                favorites.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
                }
            }
        }
}

sealed interface FavouritesIntent {
    data object LoadFavourites : FavouritesIntent
    data class ToggleFavorite(val productId: String) : FavouritesIntent
    data class OnSearchQueryChange(val query: String) : FavouritesIntent
}
