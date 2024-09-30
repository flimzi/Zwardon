package com.example.jetpacktest.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.jetpacktest.routes.Route
import com.example.jetpacktest.routes.actualRoute

@Composable
fun BottomBar(navController: NavController, screens: Collection<Route>) {
    NavigationBar {
        screens.forEach { (route, name, icon) ->
            val current by navController.currentBackStackEntryAsState()

            NavigationBarItem(
                route == current?.actualRoute,
                icon = {  },
                label = {  },
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