package com.example.jetpacktest.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetpacktest.R
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.data.Task
import com.example.jetpacktest.data.User
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.routes.singleIntParameterRoute
import com.example.jetpacktest.ui.AddTaskActionButton
import com.example.jetpacktest.ui.Content
import com.example.jetpacktest.ui.FlowScreen
import com.example.jetpacktest.ui.FullScreenDialog
import com.example.jetpacktest.ui.Header
import com.example.jetpacktest.ui.ProcessingScreen
import com.example.jetpacktest.ui.ScreenState
import com.example.jetpacktest.ui.TaskList
import com.example.jetpacktest.ui.Transform
import com.example.jetpacktest.util.Request
import com.example.jetpacktest.util.WaitRequest
import com.example.jetpacktest.util.goBack
import com.example.jetpacktest.util.navigate

@Composable
fun HomeNavHost(navHostController: NavHostController, currentUser: AuthenticatedUser, screenState: Transform<ScreenState>) {
    NavHost(navHostController, App.home.route) {
        composable(App.home.route) {
            WaitRequest(Api.Events.getUpcomingTasks(currentUser.accessToken, currentUser.id)) { tasks ->
                // run task modal
                TaskList(tasks) {  }
            }
        }

        singleIntParameterRoute(App.User.id) { _, userId ->
            if (currentUser.details.roleId == User.Role.PRIMARY && currentUser.id != userId) {
                screenState { it.copy(fab = { AddTaskActionButton { navHostController.navigate(App.Task.add, userId) } }) }
            }

            Request(Api.Users.get(currentUser.accessToken, userId)) { response ->
                Content {
                    Header(R.string.profile)
                    ProfileScreen(response.resultOrNull ?: User())
                }
            }
        }

        singleIntParameterRoute(App.Task.add) { _, userId ->
            FullScreenDialog {
                ProcessingScreen(
                    { Text("Add New Task") },
                    { navHostController.goBack() },
                    { navHostController.goBack() },
                    Task(receiver_id = userId),
                    { Api.Events.add(currentUser.accessToken, userId, it) }
                ) { _, task, onChange ->
                    Content {
                        TaskForm(currentUser, task, onChange) {
                            when (task.typeId) {
                                Task.Type.DRUG_TASK -> TaskDrugForm(currentUser, task, onChange)
                                else -> { }
                            }
                        }
                    }
                }
            }
        }

        composable(App.User.add.route) {
            FullScreenDialog {
                ProcessingScreen(
                    { Text("Add New Patient") },
                    { navHostController.goBack() },
                    { navHostController.goBack() },
                    User(),
                    { Api.Users.add(currentUser.accessToken, it) }
                ) { _, user, onChange ->
                    UserForm(user, onChange)
                }
            }
        }

        singleIntParameterRoute(App.User.edit) { _, userId ->
            FullScreenDialog {
                FlowScreen(
                    { Text("Edit User") },
                    { navHostController.goBack() },
                    { navHostController.goBack() },
                    Api.Users.get(currentUser.accessToken, userId),
                    User(),
                    { Api.Users.update(currentUser.accessToken, userId, it) }
                ) { _, user, onChange ->
                    Content {
                        UserForm(user, onChange)
                    }
                }
            }
        }
    }
}