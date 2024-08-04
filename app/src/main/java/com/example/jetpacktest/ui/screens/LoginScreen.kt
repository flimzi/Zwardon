package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.navigation.Screen

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var login by remember { mutableStateOf("") }
    var password = ""

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
            login,
            onValueChange = { login = it },
            label = { Text("Username") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button({
            authViewModel.login(login)
            navController.navigate(Screen.Home.route)
        }) {
            Text("Login")
        }

        Spacer(Modifier.height(8.dp))

        Button({ navController.navigate(Screen.Home.route) }) {
            Text("Register")
        }
    }
}