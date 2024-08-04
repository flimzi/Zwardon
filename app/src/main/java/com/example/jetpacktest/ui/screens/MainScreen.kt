package com.example.jetpacktest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.navigation.BottomBar
import com.example.jetpacktest.navigation.DrawerContent
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.navigation.TopBar
import kotlinx.coroutines.launch

@Composable
fun MainScreen(appNavController: NavController, authViewModel: AuthViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val mainNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    fun toggleDrawer() {
        coroutineScope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    ModalNavigationDrawer(
        { DrawerContent(mainNavController, listOf(Screen.Home, Screen.Profile), ::toggleDrawer) },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = { TopBar(mainNavController, Screen.Profile, ::toggleDrawer) },
            bottomBar = { BottomBar(mainNavController, listOf(Screen.Home, Screen.Profile)) },
        ) {
            Surface(Modifier.padding(it)) {
                NavHost(mainNavController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) { HomeScreen(appNavController, mainNavController) }
                    composable(Screen.Profile.route) { ProfileScreen(appNavController, authViewModel) }
                }
            }
        }
    }
}
