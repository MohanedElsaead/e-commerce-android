package com.example.route.ui.feature.favourites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.route.data.remote.dto.ProductDto
import com.example.route.ui.core.components.BottomNavTab
import com.example.route.ui.core.components.RouteBottomNavBar
import com.example.route.ui.core.components.RouteTopBar
import java.text.NumberFormat
import java.util.Locale

private val StarYellow = Color(0xFFFDD835)

@Composable
fun FavouritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavouritesViewModel = viewModel(factory = FavouritesViewModel.factory(LocalContext.current)),
    onTabSelected: (BottomNavTab) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        modifier = modifier,
        containerColor = colorScheme.secondary,
        topBar = {
            RouteTopBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = {
                    viewModel.onIntent(FavouritesIntent.OnSearchQueryChange(it))
                }
            )
        },
        bottomBar = {
            RouteBottomNavBar(
                selectedTab = BottomNavTab.Favorites,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            }

            state.favouriteProducts.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No favourites yet",
                        color = colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap the heart icon on any product to add it here",
                        color = colorScheme.tertiary,
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.favouriteProducts, key = { it.id }) { product ->
                        FavouriteProductCard(
                            product = product,
                            isFavorite = product.id in state.favoriteIds,
                            onToggleFavorite = {
                                viewModel.onIntent(FavouritesIntent.ToggleFavorite(product.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavouriteProductCard(
    product: ProductDto,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val displayPrice = product.priceAfterDiscount ?: product.price
    val hasDiscount = product.priceAfterDiscount != null && product.priceAfterDiscount < product.price

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.secondary),
        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.28f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
            ) {
                if (product.imageRes != null) {
                    Image(
                        painter = painterResource(product.imageRes),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = product.imageCover,
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(30.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(colorScheme.secondary)
                        .clickable { onToggleFavorite() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = "Favorite",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.title,
                    color = colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.description,
                    color = colorScheme.onPrimary.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "EGP ${formatPrice(displayPrice)}",
                        color = colorScheme.onPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (hasDiscount) {
                        Text(
                            text = "${formatPrice(product.price)} EGP",
                            color = colorScheme.primary.copy(alpha = 0.65f),
                            fontSize = 11.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Review (${String.format(Locale.US, "%.1f", product.ratingsAverage)})",
                        color = colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = StarYellow,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }
    return formatter.format(price)
}
