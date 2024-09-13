package com.example.jetpacktest

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.ui.screens.LoginScreen
import com.example.jetpacktest.ui.screens.MainScreen
import com.example.jetpacktest.ui.screens.RegisterScreen
import com.example.jetpacktest.ui.screens.ScanQRScreen

@Composable
fun Zwardon(appNavController: NavHostController, authViewModel: AuthViewModel) {
    val userState by authViewModel.userState.collectAsState()

    LaunchedEffect(userState) {
        if (userState == null)
            appNavController.navigate(Screen.Login.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        else
            appNavController.navigate(Screen.Home.route)
    }

    MaterialTheme(if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        // why the fuck is the surface white (looks amazing great job android team ily)
        Surface {
            NavHost(appNavController, "splash") {
                composable("splash") {
                    CircularProgressIndicator(
                        Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center))
                }
                composable(Screen.Login.route) { LoginScreen(appNavController, authViewModel) }
                composable(Screen.Register.route) { RegisterScreen(appNavController) }
                composable(Screen.Home.route) { MainScreen(appNavController, authViewModel) }
                composable(Screen.ScanQR.route) { ScanQRScreen() }
            }
        }
    }
}