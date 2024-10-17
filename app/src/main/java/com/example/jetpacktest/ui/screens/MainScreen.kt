package com.example.jetpacktest.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.R
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.navigation.DrawerContent
import com.example.jetpacktest.navigation.PrimaryAdditionalDrawerContent
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.routes.Route
import com.example.jetpacktest.routes.actualRoute
import com.example.jetpacktest.ui.StateScreen
import com.example.jetpacktest.util.navigate
import kotlinx.coroutines.launch

@Composable
fun MainScreen(appNavController: NavHostController, currentUser: AuthenticatedUser) {
    val coroutineScope = rememberCoroutineScope()
    val homeNavController = rememberNavController()
    val entry by homeNavController.currentBackStackEntryAsState()
    val currentActualRoute = entry?.actualRoute
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    fun toggleDrawer(callback: suspend () -> Unit = { }) = coroutineScope.launch {
        if (drawerState.isClosed) drawerState.open() else drawerState.close()

        callback()
    }

    fun onDrawerRoute(route: Route) {
        toggleDrawer {
            homeNavController.navigate(route.actualRoute) {
                popUpTo(App.home.route)
                launchSingleTop = true
            }
        }
    }

    ModalNavigationDrawer(
        {
            DrawerContent(
                { Text(stringResource(R.string.app_name)) },
                listOf(),
                ::onDrawerRoute,
                currentActualRoute
            ) {
                when (currentUser.details.role) {
                    1 -> PrimaryAdditionalDrawerContent(currentUser, currentActualRoute, ::onDrawerRoute)
                }
            }
        },
        drawerState = drawerState
    ) {
        StateScreen(
            { Text(stringResource(R.string.app_name)) },

            { IconButton(::toggleDrawer)
                { Icon(Icons.Filled.Menu, "Menu") } },

            { IconButton({ homeNavController.navigate(App.User.id, currentUser.id) })
                { Icon(Icons.Filled.AccountCircle, "Profile") } },
        ) {
            HomeNavHost(homeNavController, currentUser, it)
        }
    }
}