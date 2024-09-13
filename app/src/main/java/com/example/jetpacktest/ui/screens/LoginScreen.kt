package com.example.jetpacktest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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

    Scaffold(
        Modifier.padding(16.dp),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                { Text("Scan QR") },
                { Icon(Icons.Default.Face, "QR") },
                { navController.navigate(Screen.ScanQR.route) }
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(Screen.Login.name))

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                email, { email = it },
                label = { Text(stringResource(R.string.email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                password, { password = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(16.dp))

            Button({
                state = Response.Loading

                coroutineScope.launch {
                    state = if (authViewModel.login(email, password))
                        Response.Success
                    else
                        Response.Error("Login failed")
                }
            }, Modifier.fillMaxWidth()) {
                if (state is Response.Loading)
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                else
                    Text("Login")
            }

            Spacer(Modifier.height(8.dp))

            Button({ navController.navigate(Screen.Register.route) }, Modifier.fillMaxWidth()) {
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
}