package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.navigation.Screen

@Composable
fun HomeScreen(appNavController: NavHostController, homeNavController: NavController, authViewModel: AuthViewModel) {
    val user by authViewModel.user.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // this should be in the top bar changed by navigation
        Text("Home screen")

        Button(onClick = { homeNavController.navigate(Screen.Profile(user?.id!!).route) }) {
            Text("profile")
        }

        Button(onClick = { appNavController.navigate(Screen.Login.route) }) {
            Text("login")
        }
    }
}