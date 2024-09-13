package com.example.jetpacktest.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.jetpacktest.R
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.User
import io.ktor.client.call.body
import kotlinx.coroutines.launch

@Composable
fun DrawerRoute(navController: NavController, drawerState: DrawerState, label: String, route: String, icon: ImageVector? = null) {
    val coroutineScope = rememberCoroutineScope()
    val currentRoute by navController.currentBackStackEntryAsState()

    NavigationDrawerItem(
        { Text(label) },
        selected = route == currentRoute?.actualRoute,
        onClick = {
            navController.navigate(route)
            coroutineScope.launch { drawerState.close() }
        },
        icon = { if(icon != null) Icon(icon, label) },
        shape = RectangleShape
    )
}

@Composable
fun DrawerRoute(navController: NavController, drawerState: DrawerState, screen: Screen) {
    DrawerRoute(navController, drawerState, stringResource(screen.name), screen.route, screen.icon)
}

@Composable
fun DrawerContent(navController: NavController, authViewModel: AuthViewModel, drawerState: DrawerState, screens: Collection<Screen>) {
    val secondaries = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        val users = Api.Users.getSecondary(authViewModel.accessToken, authViewModel.user.id).body<List<User>>()
        secondaries.addAll(users)
    }

    ModalDrawerSheet {
        Text(stringResource(R.string.app_name), modifier = Modifier.padding(16.dp))

        screens.forEach { DrawerRoute(navController, drawerState, it) }

        if (authViewModel.user.role == 1) {
            HorizontalDivider()
            DrawerRoute(navController, drawerState, Screen.AddUser)

            // list secondary users (not sure how they should update though, i guess a websocket would be ideal)
            secondaries.forEach { user ->
                DrawerRoute(
                    navController, drawerState,
                    "${user.first_name} ${user.last_name}",
                    Screen.Profile(user.id).route,
                    Icons.Filled.Person
                )
            }
        }
    }
}