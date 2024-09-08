package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.User
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.util.Response
import io.ktor.client.call.body
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(appNavController: NavController, authViewModel: AuthViewModel, userId: Int) {
    val coroutineScope = rememberCoroutineScope()
    var state by remember { mutableStateOf<Response<User>>(Response.Loading) }
    var dialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        state = try {
            val user = Api.Users.get(authViewModel.accessToken.value!!, userId).body<User>()
            Response.Result(user)
        } catch (e : Exception) {
            authViewModel.logout()
            Response.Error("Server error")
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (state) {
            Response.Loading -> CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            is Response.Result<User> -> {
                val user = (state as Response.Result<User>).result

                Text("Profile Screen ${user.id}")

                if (user.email != null)
                    Text(user.email)

                Button({
                    appNavController.navigate(Screen.AddTask(userId).route)
                }) {
                    Text("add task")
                }

                Button({
                    coroutineScope.launch {
                        authViewModel.login(Api.Users.token(authViewModel.accessToken.value!!, user.id).body())
                    }
                }) {
                    Text("log in")
                }

                Button({
                    coroutineScope.launch { authViewModel.logout() }
                }) {
                    Text("log out")
                }

                Button({ dialogVisible = true }) {
                    Text("Launch")
                }
            }
            else -> { }
        }
    }
}