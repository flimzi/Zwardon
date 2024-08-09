package com.example.jetpacktest

import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.ui.screens.LoginScreen
import com.example.jetpacktest.ui.screens.MainScreen

@Composable
fun Zwardon(appNavController: NavHostController, authViewModel: AuthViewModel) {
    MaterialTheme(if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
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