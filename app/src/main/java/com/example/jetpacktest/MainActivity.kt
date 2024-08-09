package com.example.jetpacktest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.util.HttpClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity : ComponentActivity() {
    private lateinit var appNavController: NavHostController
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}


