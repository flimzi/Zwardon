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
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.util.Response
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
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
        Text(stringResource(Screen.Login.name))

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            email, { email = it },
            label = { Text(stringResource(R.string.email)) }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            password, { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button({
            coroutineScope.launch {
                state = Response.Loading

                state = try {
                    if (authViewModel.login(email, password))
                        Response.Success
                    else
                        Response.Error("Login failed")
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
                Text("Login")
        }

        Spacer(Modifier.height(8.dp))

        Button({ navController.navigate(Screen.Register.route) }) {
            Text("Register")
        }

        when (val currentState = state) {
            is Response.Error -> {
                LaunchedEffect(state) {
                    Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                }
            }
            else -> { }
        }
    }
}