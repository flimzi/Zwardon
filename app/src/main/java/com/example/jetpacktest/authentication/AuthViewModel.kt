package com.example.jetpacktest.authentication

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.util.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class AuthViewModel : ViewModel() {
    var user: User? by mutableStateOf(null)

    fun login(login: String) {
        user = User(login)
    }

    fun logout() {
        user = null
    }

    fun verifyUser(verificationToken: String, onResult: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:3000/api/auth/verify"
        val json = """{ "token": "$verificationToken" }"""

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()

        HttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    return onResult(true)
                }

                onResult(false)
            }
        })
    }

// synchronous code example
//    fun verifyToken(token: String, onResult: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            val isSuccess = withContext(Dispatchers.IO) {
//                try {
//                    val url = "https://example.com/verify?token=$token"
//                    val request = Request.Builder()
//                        .url(url)
//                        .build()
//
//                    val response = client.newCall(request).execute()
//                    val isSuccess = response.isSuccessful
//                    response.close()
//                    isSuccess
//                } catch (e: IOException) {
//                    // Handle the error
//                    false
//                }
//            }
//            onResult(isSuccess)
//        }
//    }
}