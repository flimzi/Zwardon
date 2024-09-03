package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.User
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.util.Response
import io.ktor.client.call.body
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(userId: Int, appNavController: NavController, authViewModel: AuthViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var state by remember { mutableStateOf<Response<User>>(Response.Loading) }

    LaunchedEffect(Unit) {
        state = try {
            val user = Api.Users.get(authViewModel.accessToken.value!!, userId).body<User>()
            Response.Result(user)
        } catch (e : Exception) {
            authViewModel.logout()
            Response.Error("Server error")
        }
    }

    when (state) {
        Response.Loading -> CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        is Response.Result<User> -> {
            val user = (state as Response.Result<User>).data

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Profile Screen")

                if (user.email != null)
                    Text(user.email)

                Button({
                    coroutineScope.launch { authViewModel.logout() }
                }) {
                    Text("log out")
                }
            }
        }
        else -> { }
    }
}