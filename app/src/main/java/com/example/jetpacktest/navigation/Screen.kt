package com.example.jetpacktest.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jetpacktest.R

object RouteParameters {
    const val USER_ID = "userId"
}

// there should be more fine grained classes
// also this is kinda misleading because login is in a different navController so...
sealed class Screen(val route: String, @StringRes val name: Int, val icon: ImageVector) {
    data object Home : Screen("home", R.string.home, Icons.Filled.Home)
    data object Login : Screen("login", R.string.login, Icons.Filled.Lock)
    data object Register : Screen("register", R.string.register, Icons.Filled.Lock)
    class Profile(private val userId: Int) : Screen("user/${userId}/profile", R.string.profile, Icons.Filled.AccountCircle)
    data object AddUser : Screen("addUser", R.string.addUser, Icons.Filled.AccountCircle)

    operator fun component1() = route
    operator fun component2() = name
    operator fun component3() = icon
}