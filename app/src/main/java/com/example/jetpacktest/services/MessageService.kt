package com.example.jetpacktest.services
import android.util.Log
import com.example.jetpacktest.extensions.await
import com.example.jetpacktest.extensions.postJson
import com.example.jetpacktest.util.HttpClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.tasks.asDeferred
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MessageService : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()

        Log.d("service", "created")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("service", "message received")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("service", "token refresh")
    }

    // this will probably need to be on User model or at least accept it as argument
//    suspend fun sendToken() {
//        val token = FirebaseMessaging.getInstance().token.asDeferred().await()
//        val credentials = object {
//            val email = "420skun@gmail.com"
//            val password = "Abc123"
//        }
//
//        val accessToken = HttpClient.postJson("http://10.0.2.2/api/auth/login", credentials).await().getOrNull()?.body?.string() ?: ""
//
//        HttpClient.postJson("http://10.0.2.2:3000/api/user/fcm", token)
//    }
}