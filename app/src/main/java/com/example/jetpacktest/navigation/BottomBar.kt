package com.example.jetpacktest.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState

val NavBackStackEntry.actualRoute: String?
    get() = destination.route?.let {
        it.split("/").joinToString("/") { segment ->
            if (segment.startsWith("{") && segment.endsWith("}"))
                arguments?.get(segment.removeSurrounding("{", "}"))?.toString() ?: segment
            else
                segment
        }
    }

@Composable
fun BottomBar(navController: NavController, screens: Collection<Screen>) {
    NavigationBar {
        screens.forEach { (route, name, icon) ->
            val current by navController.currentBackStackEntryAsState()

            NavigationBarItem(
                route == current?.actualRoute,
                icon = { Icon(icon, contentDescription = stringResource(name)) },
                label = { Text(stringResource(name)) },
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