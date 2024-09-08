package com.example.jetpacktest.ui.screens

import androidx.compose.runtime.Composable
import com.example.jetpacktest.util.CameraPermission
import com.example.jetpacktest.util.QRPreview

@Composable
fun ScanQRScreen() {
    CameraPermission {
        QRPreview {
            val x = it
        }
    }
}