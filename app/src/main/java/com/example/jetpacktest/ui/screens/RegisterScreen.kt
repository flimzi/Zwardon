package com.example.jetpacktest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacktest.R
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.User
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.util.Response
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var state by remember { mutableStateOf<Response<Nothing>>(Response.Idle) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(Screen.Register.name))

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            email, { email = it.trim() },
            label = { Text(stringResource(R.string.email)) }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            password, { password = it.trim() },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button({
            coroutineScope.launch {
                state = Response.Loading

                state = try {
                    val user = User(0, 1, email = email, password = password)
                    val response = Api.Users.add(user)

                    when (response.status) {
                        HttpStatusCode.Created -> Response.Success
                        HttpStatusCode.Conflict -> Response.Error("E-mail already exists")
                        HttpStatusCode.BadRequest -> Response.Error("Check your input information and try again")
                        else -> Response.Error("Server error")
                    }
                } catch (e: Exception) {
                    Response.Error("Server error")
                }
            }
        }) {
            if (state is Response.Loading)
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            else
                Text(stringResource(Screen.Register.name))
        }

        Spacer(Modifier.height(8.dp))

        Button({ navController.navigate(Screen.Home.route) }) {
            Text(stringResource(Screen.Login.name))
        }

        when (val currentState = state) {
            is Response.Error -> {
                LaunchedEffect(state) {
                    Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                }
            }
            Response.Success -> {
                navController.navigate(Screen.Home.route)

                LaunchedEffect(state) {
                    Toast.makeText(context, "Registration successful. Log in to continue", Toast.LENGTH_LONG).show()
                }
            }
            else -> { }
        }
    }
}