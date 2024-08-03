package com.example.jetpacktest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.graphics.vector.ImageVector

// the name and icon could be like defined in Menu.kt but whatever
sealed class Screen(val route: String, val name: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Filled.Home)
    data object Login : Screen("login", "Login", Icons.Filled.Lock)
    data object Profile : Screen("profile", "Profile", Icons.Filled.AccountCircle)

    operator fun component1() = route
    operator fun component2() = name
    operator fun component3() = icon
}