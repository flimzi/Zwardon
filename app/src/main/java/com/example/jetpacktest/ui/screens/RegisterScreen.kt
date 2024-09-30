package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacktest.R
import com.example.jetpacktest.myApplication
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.ui.Screen
import com.example.jetpacktest.util.RequestButton
import com.example.jetpacktest.util.rememberAnyResponse

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = context.myApplication.authRepository

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var response by rememberAnyResponse()
    val enabled = email.isNotBlank() && password.isNotBlank()

    Screen(
        { Text("Register") },
        response,
        rightAction = {
            TextButton(onClick = { navController.navigate(App.Authentication.login.route) }) {
                Text("Login")
            }
        }
    ) {
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            email, { email = it.trim() }, Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.email)) }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            password, { password = it.trim() }, Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            RequestButton(authRepository.register(email, password), { response = it }, enabled = enabled) {
                Text("Register")
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}