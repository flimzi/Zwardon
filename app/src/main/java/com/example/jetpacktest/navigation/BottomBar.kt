package com.example.jetpacktest.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// the screen collection is a loose contract because not all screens are going to be in a given navhost
// obviously the routes provided externally need to be placed inside the navController graph
@Composable
fun BottomBar(navController: NavController, screens: Collection<Screen>) {
    NavigationBar {
        screens.forEach { (route, name, icon) ->
            NavigationBarItem(
                route == navController.currentBackStackEntryAsState().value?.destination?.route,
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