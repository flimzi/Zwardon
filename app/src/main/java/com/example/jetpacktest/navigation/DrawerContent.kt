package com.example.jetpacktest.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.routes.Route
import com.example.jetpacktest.util.ListRequest

@Composable
fun DrawerRoute(
    route: Route,
    currentActualRoute: String? = null,
    onClick: (Route) -> Unit = { },
    label: @Composable () -> Unit = route.name
) {
    NavigationDrawerItem(
        label,
        currentActualRoute == route.actualRoute,
        { onClick(route) },
        icon = { Icon(route.icon, route.actualRoute) },
        shape = RectangleShape
    )
}

@Composable
fun DrawerContent(
    title: @Composable () -> Unit = { },
    routes: Collection<Route> = listOf(),
    onRoute: (Route) -> Unit = { },
    currentActualRoute: String? = null,
    additionalContent: @Composable () -> Unit = { }
) {
    ModalDrawerSheet {
        title()
        routes.forEach { DrawerRoute(it, currentActualRoute) { onRoute(it) } }
        HorizontalDivider()
        additionalContent()
    }
}

@Composable
fun PrimaryAdditionalDrawerContent(currentUser: AuthenticatedUser, currentActualRoute: String?, onRoute: (Route) -> Unit) {
    Column {
        DrawerRoute(App.User.add, currentActualRoute, onRoute) { Text("Add New Patient") }

        ListRequest(Api.Users.getChildren(currentUser.accessToken, currentUser.id), currentActualRoute) { child ->
            DrawerRoute(App.User.id.replace(child.id), currentActualRoute, onRoute) { Text(child.fullName) }
        }
    }
}