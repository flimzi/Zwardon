package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacktest.R
import com.example.jetpacktest.myApplication
import com.example.jetpacktest.routes.App
import com.example.jetpacktest.ui.Layout

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = context.myApplication.authRepository

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Layout(
        { Text("Login") },
        rightAction = {
            TextButton(onClick = { navController.navigate(App.Authentication.register.route) }) {
                Text("Register")
            }
        }
    ) {
        OutlinedTextField(
            email, { email = it }, Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            password, { password = it }, Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//            RequestButton({ Text("Login") }, authRepository.login(email, password)) {
//
//            }
//        }
    }
}