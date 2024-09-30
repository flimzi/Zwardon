package com.example.jetpacktest.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.R
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.data.User
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.routes.singleIntParameterRoute
import com.example.jetpacktest.ui.Layout
import com.example.jetpacktest.util.WaitRequest
import com.example.jetpacktest.util.eager
import com.example.jetpacktest.util.wait
import kotlinx.coroutines.launch

@Composable
fun MainScreen(appNavController: NavHostController, currentUser: AuthenticatedUser) {
    val coroutineScope = rememberCoroutineScope()
    val homeNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        { /* DrawerContent(homeNavController, currentUser, drawerState, listOf(App.User.id.replace(currentUser.details.id))) */ },
        drawerState = drawerState
    ) {
        Layout(
            { Text(stringResource(R.string.app_name)) },
            leftAction = {
                IconButton(
                    {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            },
            rightAction = {
                IconButton({ homeNavController.navigate(App.User.id.replace(currentUser.details.id).route) }) {
                    Icon(App.User.id.icon, "Profile")
                }
            }
        ) {
            NavHost(homeNavController, App.home.route) {
                // would be nice to somehow allow for filling the entire scrollable content space
                // also in terms of screens that are not their own dialogs but instead content inside the main dialog some structure needs to be provided
                composable(App.home.route) {
//                    Api.Users.get(currentUser.accessToken, -994).eager(::state) { user ->
//                        Text(user?.email ?: "no email")
//                    }
//
//                    Api.Users.get(currentUser.accessToken, -994).wait { user ->
//                        Text(user.email ?: "no email")
//                    }
//
//                    Api.Users.getChildren(currentUser.accessToken, 1000).list {
//
//                    }

                    Button({ homeNavController.navigate(App.User.edit.replace(200).route) }) {
                        Text("go forth")
                    }
                }

                singleIntParameterRoute(App.User.edit) { _, userId ->
                    // i need to redirect the results of the requestflow to the resulting dialog
                    // i think this should probably be done by separating Layout layout from its state
                    // so that it could then be mediated by a parent response handler
                    Api.Users.get(currentUser.accessToken, userId).eager(::state) { user ->
                        EditUserDialog(user ?: User())
                    }
                }

                singleIntParameterRoute(App.User.id) { _, userId ->
                    // ApiRepository could be used to optionally cache responses with a lifetime
                    WaitRequest(Api.Users.get(currentUser.accessToken, userId)) { user ->
                        ProfileScreen(appNavController, currentUser, user)
                    }
                }
            }
        }
    }
}
