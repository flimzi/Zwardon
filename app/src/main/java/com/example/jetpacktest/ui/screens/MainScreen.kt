package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.jetpacktest.ui.ActionScreen
import com.example.jetpacktest.ui.ActionScreenState
import com.example.jetpacktest.ui.Content
import com.example.jetpacktest.ui.FlowScreen
import com.example.jetpacktest.ui.FullScreenDialog
import com.example.jetpacktest.ui.Header
import com.example.jetpacktest.ui.Screen
import com.example.jetpacktest.ui.Transform
import com.example.jetpacktest.util.Request
import com.example.jetpacktest.util.Response
import com.example.jetpacktest.util.goBack
import com.example.jetpacktest.util.response
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
                    Request(Api.Users.get(currentUser.accessToken, 707)) { response ->
                        ActionScreen(onAction = { state -> state { it.copy(actionLoading = !it.actionLoading) } }, initialState = ActionScreenState(message = "xd")) { state ->
                            Column {
                                Text(response.resultOrNull?.email ?: "email here")

                                Button({ state { it.copy(actionLoading = !it.actionLoading) } }) {
                                    Text("set loading")
                                }
                            }
                        }
                    }

                    Button({ homeNavController.navigate("test") }) {
                        Text("go forth")
                    }
                }

                // trying to remake user edit using actionscreen instead of confirmationscreen/processingscreen
                // IT FUCKING WORKS
                // this is so good that i dont even know if its worth abstracting this any further
                composable("test") {
                    val coroutineScope = rememberCoroutineScope()

                    fun onAction(state: Transform<ActionScreenState>, newUser: User) {
                        coroutineScope.launch {
                            Api.Users.update(currentUser.accessToken, currentUser.id, newUser)
                                .response { homeNavController.goBack() }
                                .collect { response ->
                                    state {
                                        it.copy(
                                            actionLoading = response is Response.Loading,
                                            message = if (response is Response.Error) response.message else it.message
                                        )
                                    }
                                }
                        }
                    }

                    FullScreenDialog {
                        Request(Api.Users.get(currentUser.accessToken, currentUser.id)) { response ->
                            var user by remember { mutableStateOf<User?>(null) }
                            val newUser = user

                            ActionScreen(
                                { Text("Edit User") },
                                { homeNavController.goBack() },
                                { if (newUser != null) onAction(it, newUser) },
                                ActionScreenState(actionEnabled = newUser?.email?.isNotBlank() == true).mergeWith(response)
                            ) {
                                Content {
                                    UserForm(response.resultOrNull ?: User()) { user = it }
                                }
                            }
                        }
                    }
                }

                singleIntParameterRoute(App.User.id) { _, userId ->
                    Request(Api.Users.get(currentUser.accessToken, userId)) { response ->
                        Content {
                            Header(R.string.profile)
                            ProfileScreen(response.resultOrNull ?: User())
                        }
                    }
                }

                singleIntParameterRoute(App.User.edit) { _, userId ->
                    FullScreenDialog {
                        FlowScreen(
                            { Text("Edit User") },
                            { homeNavController.popBackStack() },
                            { homeNavController.popBackStack() },
                            Api.Users.get(currentUser.accessToken, userId),
                            { Api.Users.update(currentUser.accessToken, userId, it) },
                            { it.first_name?.isNotBlank() == true }
                        ) { user, onState ->
                            Content {
                                UserForm(user ?: User()) { onState(Response.Result(it)) }
                            }
                        }
                    }
                }
            }
        }
    }
}
