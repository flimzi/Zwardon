package com.example.jetpacktest

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.extensions.addBearer
import com.example.jetpacktest.extensions.await
import com.example.jetpacktest.extensions.call
import com.example.jetpacktest.extensions.postJson
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.util.HttpClient
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.asDeferred
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class MainActivity : ComponentActivity() {
    private lateinit var appNavController: NavHostController
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()

        runBlocking {
            sendToken()
        }

        setContent {
            appNavController = rememberNavController()
            authViewModel = viewModel()

            Zwardon(appNavController, authViewModel)
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let {
            handleIntent(it)
        }
    }

    private fun handleIntent(intent: Intent) {
        appNavController.navigate(Screen.Login.route) {
            popUpTo(Screen.Login.route) { inclusive = true }
        }

        Toast.makeText(this, R.string.verifying, Toast.LENGTH_SHORT).show()

        if (intent.action == Intent.ACTION_VIEW && intent.data?.path == "/verification") {
            intent.data?.getQueryParameter("token")?.let {
                authViewModel.verifyUser(it) { isSuccess ->
                    val message = when (isSuccess) {
                        true -> R.string.verification_success
                        else -> R.string.verification_failure
                    }

                    runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            return
        }
    }

    suspend fun sendToken() {
        val token = FirebaseMessaging.getInstance().token.asDeferred().await()
        val credentials = object {
            val email = "420skun@gmail.com"
            val password = "Abc123"
        }

        val accessToken = HttpClient.postJson("http://10.0.2.2:3000/api/auth/login", credentials).await().getOrNull()?.body?.string() ?: ""

        val response = Request.Builder()
            .url("http://10.0.2.2:3000/api/auth/fcm")
            .addBearer(accessToken)
            .post(token.toRequestBody("text/plain".toMediaType()))
            .call(HttpClient)
            .await()
            .getOrNull()

        if (!response?.isSuccessful!!) {
            Log.w("fcm token", "cannot update")
            return
        }

        Request.Builder()
            .url("http://10.0.2.2:3000/api/user/fcmTest?fcm=$token")
            .addBearer(accessToken)
            .get()
            .call(HttpClient)
            .await()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            return

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}


