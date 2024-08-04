package com.example.jetpacktest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.ui.screens.LoginScreen
import com.example.jetpacktest.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                val appNavController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                Surface {
                    NavHost(appNavController, startDestination = Screen.Login.route) {
                        composable(Screen.Login.route) { LoginScreen(appNavController, authViewModel) }
                        composable(Screen.Home.route) {
                            if (authViewModel.user == null) {
                                appNavController.navigate((Screen.Login.route)) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                MainScreen(appNavController, authViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}


