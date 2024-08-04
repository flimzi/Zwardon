package com.example.jetpacktest.authentication

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var user: User? by mutableStateOf(null)

    fun login(login: String) {
        user = User(login)
    }

    fun logout() {
        user = null
    }
}