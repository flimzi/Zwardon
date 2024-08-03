package com.example.jetpacktest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.jetpacktest.navigation.Menu
import com.example.jetpacktest.navigation.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    // cant this be in menu?
    val menuNavController = rememberNavController()

    Scaffold(
        bottomBar = { Menu(menuNavController, listOf(Screen.Home, Screen.Profile)) }
    ) {
        NavHost(menuNavController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) { HomeScreen(navController, menuNavController) }
            composable(Screen.Profile.route) { ProfileScreen(menuNavController) }
        }
    }
}
