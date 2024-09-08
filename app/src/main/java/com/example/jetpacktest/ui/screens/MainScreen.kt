package com.example.jetpacktest.ui.screens

import android.media.RouteListingPreference
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.TaskViewModel
import com.example.jetpacktest.data.TaskViewModelFactory
import com.example.jetpacktest.navigation.BottomBar
import com.example.jetpacktest.navigation.DrawerContent
import com.example.jetpacktest.navigation.RouteParameters
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.navigation.TopBar
import kotlinx.coroutines.launch

@Composable
fun MainScreen(appNavController: NavHostController, authViewModel: AuthViewModel) {
    val taskViewModel = viewModel<TaskViewModel>(factory = TaskViewModelFactory(authViewModel))
    val coroutineScope = rememberCoroutineScope()
    val homeNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val user by authViewModel.user.collectAsState()

    fun toggleDrawer() {
        coroutineScope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    ModalNavigationDrawer(
        { DrawerContent(homeNavController, authViewModel, drawerState, listOf(Screen.Home, Screen.Profile(user?.id!!))) },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = { TopBar(homeNavController, Screen.Profile(user?.id!!), ::toggleDrawer) },
//            bottomBar = { BottomBar(homeNavController, listOf(Screen.Home, Screen.Profile(user?.id!!))) },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            Surface(Modifier.padding(it)) {
                NavHost(homeNavController, Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeScreen(appNavController, homeNavController, authViewModel)
                    }

                    composable(Screen.AddUser.route) {
                        AddUserScreen(homeNavController, authViewModel)
                    }

                    composable(
                        "user/{userId}/profile",
                        listOf(navArgument(RouteParameters.USER_ID) { type = NavType.IntType })
                    ) {
                        ProfileScreen(homeNavController, authViewModel, it.arguments?.getInt(RouteParameters.USER_ID)!!)
                    }

                    composable(
                        "user/{userId}/task",
                        listOf(navArgument(RouteParameters.USER_ID) { type = NavType.IntType })
                    ) {
//                        AddTaskScreen(homeNavController, authViewModel, it.arguments?.getInt(RouteParameters.USER_ID)!!)
                        AddTaskDialog(homeNavController, authViewModel, taskViewModel, it.arguments?.getInt(RouteParameters.USER_ID)!!)
                    }
                }
            }
        }
    }
}
