package com.example.jetpacktest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.User
import com.example.jetpacktest.navigation.Screen
import com.github.javafaker.Faker
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var appNavController: NavHostController
    private val authViewModel: AuthViewModel by viewModels { AuthViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()

        setContent {
            appNavController = rememberNavController()

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
        if (intent.action == Intent.ACTION_VIEW && intent.data?.path == "/verification") {
            appNavController.navigate(Screen.Login.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }

            Toast.makeText(this, R.string.verifying, Toast.LENGTH_SHORT).show()

            intent.data?.getQueryParameter("token")?.let {
                runBlocking { }
            }

            return
        }

        if (intent.hasExtra("ACTION_DETAILS")) {
            val actionDetails = intent.getStringExtra("ACTION_DETAILS")
            val x = 2
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted)
            return@registerForActivityResult

        openNotificationSettings()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            return

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun openNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
            }
        }

        startActivity(intent)
    }
}


