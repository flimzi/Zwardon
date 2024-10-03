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
import com.example.jetpacktest.ui.Content
import com.example.jetpacktest.ui.FullScreenDialog
import com.example.jetpacktest.ui.ResponseScreen
import com.example.jetpacktest.ui.Screen
import com.example.jetpacktest.util.Request
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
        Screen(
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
                IconButton({ homeNavController.navigate(App.User.id.replace(currentUser.id).route) }) {
                    Icon(App.User.id.icon, "Profile")
                }
            }
        ) {
            NavHost(homeNavController, App.home.route) {
                composable(App.home.route) {
                    Button({ homeNavController.navigate(App.User.edit.replace(currentUser.id).route) }) {
                        Text("go forth")
                    }
                }

                singleIntParameterRoute(App.User.edit) { _, userId ->
                    FullScreenDialog {
                        ResponseScreen(
                            { Text("Edit User") },
                            { homeNavController.popBackStack() },
                            { homeNavController.popBackStack() },
                            Api.Users.get(currentUser.accessToken, userId),
                            { Api.Users.update(currentUser.accessToken, userId, it) },
                            { it.first_name?.isNotBlank() == true }
                        ) { user, onChange, onMessage ->
                            Content {
                                UserForm(user ?: User(), onChange)
                            }
                        }
                    }
                }

                singleIntParameterRoute(App.User.id) { _, userId ->
                    // ApiRepository could be used to optionally cache responses with a lifetime
                    Request(Api.Users.get(currentUser.accessToken, userId)) { response ->
                        Screen(
                            { Text("Profile") },
                            state = response
                        ) {
                            Content {

                            }
                        }
                    }
                }
            }
        }
    }
}
