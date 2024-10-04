package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.data.User
import com.example.jetpacktest.myApplication
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.routes.App.User.userId
import com.example.jetpacktest.routes.navigate
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(user: User) {
    Text(user.fullName)
}

@Composable
fun ProfileScreen(appNavController: NavController, currentUser: AuthenticatedUser, user: User = currentUser.details) {
    val context = LocalContext.current
    val authRepository = context.myApplication.authRepository
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Profile Screen ${user.id}")

        if (user.email != null)
            Text(user.email)

        Button({ appNavController.navigate(App.Task.add, userId) }) {
            Text("add task")
        }

        Button({
            coroutineScope.launch { authRepository.logout() }
        }) {
            Text("log out")
        }
    }
}