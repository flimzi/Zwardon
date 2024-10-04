package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoadingIndicator() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
fun LoadingBox(transparent: Boolean = false) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = if (transparent) 0.5f else 1f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) { LoadingIndicator() }
}

@Composable
fun LoadingScreen(transparent: Boolean = false) {
    Dialog(onDismissRequest = { }, DialogProperties(usePlatformDefaultWidth = false)) {
        LoadingBox(transparent)
    }
}

//@Composable
//fun Loading(fullScreen: Boolean = false) = if (fullScreen) LoadingScreen() else LoadingIndicator()