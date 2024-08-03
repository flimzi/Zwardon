package com.example.jetpacktest.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun Menu(navController: NavController, screens: Collection<Screen>) {
    NavigationBar {
        screens.forEach { (route, name, icon) ->
            NavigationBarItem(
                route == navController.currentBackStackEntryAsState().value?.destination?.route,
                icon = { Icon(icon, contentDescription = name) },
                label = { Text(name) },
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}