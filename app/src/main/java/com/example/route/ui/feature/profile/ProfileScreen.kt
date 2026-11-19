package com.example.route.ui.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.route.ui.core.components.BottomNavTab
import com.example.route.ui.core.components.RouteBottomNavBar

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(LocalContext.current)),
    onLogoutSuccess: () -> Unit = {},
    onTabSelected: (BottomNavTab) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = colorScheme.secondary,
        bottomBar = {
            RouteBottomNavBar(
                selectedTab = BottomNavTab.Profile,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar
            val initials = state.userName
                .split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .joinToString("")
                .ifEmpty { "U" }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = colorScheme.secondary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = state.userName.ifBlank { "User" },
                color = colorScheme.onPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // User Email
            if (state.userEmail.isNotBlank()) {
                Text(
                    text = state.userEmail,
                    color = colorScheme.tertiary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Cards
            ProfileInfoCard(
                icon = Icons.Outlined.Person,
                title = "Your Account",
                subtitle = "Manage your personal information"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                icon = Icons.Outlined.Email,
                title = "Email",
                subtitle = state.userEmail.ifBlank { "Not set" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                icon = Icons.Outlined.ShoppingBag,
                title = "Your Orders",
                subtitle = "View your order history"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            Button(
                onClick = { viewModel.onIntent(ProfileIntent.Logout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoggingOut
            ) {
                if (state.isLoggingOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colorScheme.secondary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = null,
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        color = colorScheme.secondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.secondary),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = colorScheme.onPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = colorScheme.tertiary,
                    fontSize = 13.sp
                )
            }
        }
    }
}
