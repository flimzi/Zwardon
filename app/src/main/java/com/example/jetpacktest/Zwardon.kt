package com.example.jetpacktest

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.ui.screens.LoadingBox
import com.example.jetpacktest.ui.screens.LoginScreen
import com.example.jetpacktest.ui.screens.MainScreen
import com.example.jetpacktest.ui.screens.RegisterScreen
import com.example.jetpacktest.util.Response

@Composable
fun Zwardon(appNavController: NavHostController) {
    val context = LocalContext.current
    val userState by context.myApplication.authRepository.userState.collectAsState()
    val user = userState.resultOrNull

    // this needs to be rethinked because the load dialog is overstaying its welcome by a long shot
    val startRoute = if (userState is Response.Loading)
        App.loading
    else if (user == null)
        App.Authentication.login
    else
        App.home

    MaterialTheme(if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        NavHost(
            appNavController,
            startRoute.route,
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable(App.Authentication.login.route) {
                LoginScreen(appNavController)
            }

            composable(App.Authentication.register.route) {
                RegisterScreen(appNavController)
            }

            composable(App.loading.route) {
                LoadingBox()
            }

            if (user != null) {
                composable(App.home.route) {
                    MainScreen(appNavController, user)
                }
            }
        }
    }
}