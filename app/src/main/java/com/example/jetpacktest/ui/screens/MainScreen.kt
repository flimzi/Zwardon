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
import com.example.jetpacktest.ui.FullScreenDialog
import com.example.jetpacktest.ui.ProcessingScreen
import com.example.jetpacktest.ui.Screen
import com.example.jetpacktest.util.WaitRequest
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
//                    FullScreenDialog {
//                        val scope = rememberCoroutineScope()
//                        var response by rememberResponse<User>()
//
//                        LaunchedEffect(Unit) {
//                            Api.Users.get(currentUser.accessToken, userId).collect { response = it }
//                        }
//
//                        UserForm(
//                            { Text("Edit User") },
//                            response,
//                            { },
//                            {
//                                Api.Users.update(currentUser.accessToken, userId, it).collect { response = it }
//                            }
//                        )
//                    }

                    FullScreenDialog {
                        ProcessingScreen(
                            input = Api.Users.get(currentUser.accessToken, userId),
                            process = { Api.Users.update(currentUser.accessToken, userId, it) },
                            enabler = { it.first_name?.isNotBlank() == true }
                        ) { user, state ->
                            UserForm(user ?: User()) { state(it) }
                        }
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
