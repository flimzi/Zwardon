package com.example.jetpacktest.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.jetpacktest.R
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.routes.Route
import com.example.jetpacktest.routes.actualRoute
import com.example.jetpacktest.util.ListRequest
import kotlinx.coroutines.launch

@Composable
fun DrawerRoute(navController: NavController, drawerState: DrawerState, route: String, icon: ImageVector? = null, label: @Composable () -> Unit = { }) {
    val coroutineScope = rememberCoroutineScope()
    val currentRoute by navController.currentBackStackEntryAsState()

    NavigationDrawerItem(
        label,
        selected = route == currentRoute?.actualRoute,
        onClick = {
            navController.navigate(route)
            coroutineScope.launch { drawerState.close() }
        },
        icon = { if(icon != null) Icon(icon, "") },
        shape = RectangleShape
    )
}

@Composable
fun DrawerRoute(navController: NavController, drawerState: DrawerState, route: Route) {
    DrawerRoute(navController, drawerState, route.route, route.icon, route.name)
}

@Composable
fun DrawerContent(
    navController: NavController,
    currentUser: AuthenticatedUser,
    drawerState: DrawerState,
    routes: Collection<Route>,
) {
    ModalDrawerSheet {
        Text(stringResource(R.string.app_name), modifier = Modifier.padding(16.dp))

        routes.forEach { DrawerRoute(navController, drawerState, it) }

        if (currentUser.details.role == 1) {
            HorizontalDivider()
            DrawerRoute(navController, drawerState, App.User.add)

            // this should not be a full screen loading screen and instead just in place of content
            // so it should be a parameter on Request
            ListRequest(Api.Users.getChildren(currentUser.accessToken, currentUser.details.id)) { child ->
                DrawerRoute(
                    navController, drawerState,
                    App.User.id.copy(name = { Text("${child.first_name} ${child.last_name}") }).replace(child.id)
                )
            }
        }
    }
}