package com.example.jetpacktest

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.User
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.ui.screens.LoginScreen
import com.example.jetpacktest.ui.screens.MainScreen
import com.example.jetpacktest.ui.screens.RegisterScreen
import com.example.jetpacktest.util.Response

@Composable
fun Zwardon(appNavController: NavHostController, authViewModel: AuthViewModel) {
    val userState by authViewModel.userState.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userState) {
        if (userState is Response.Result) {
            if ((userState as? Response.Result<User?>)?.data != null) {
                appNavController.navigate(Screen.Home.route)
            } else {
                appNavController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }

    MaterialTheme(if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        // why the fuck is the surface white (looks amazing great job android team ily)
        Surface {
            NavHost(appNavController, startDestination = "splash") {
                composable("splash") {
                    CircularProgressIndicator(
                        Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center))
                }
                composable(Screen.Login.route) { LoginScreen(appNavController, authViewModel) }
                composable(Screen.Register.route) { RegisterScreen(appNavController) }
                composable(Screen.Home.route) { MainScreen(appNavController, authViewModel) }
            }
        }
    }
}