package com.example.jetpacktest.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.jetpacktest.ui.screens.LoadingBox
import com.example.jetpacktest.util.Response
import com.example.jetpacktest.util.rememberAnyResponse
import kotlinx.coroutines.launch

interface LayoutController {
    fun snackbar(message: String)
    fun state(response: Response<*>)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout(
    label: @Composable () -> Unit = { },
    leftAction: @Composable () -> Unit = { },
    rightAction: @Composable RowScope.() -> Unit = { },
    content: @Composable LayoutController.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var responseState by rememberAnyResponse()

    val controller = object : LayoutController {
        override fun snackbar(message: String) {
            coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(message)
            }
        }

        override fun state(response: Response<*>) {
            responseState = response

            if (response is Response.Error) {
                snackbar(response.message)
            }
        }
    }

    if (responseState is Response.Loading) {
        LoadingBox(true)
    }

    Scaffold(
        Modifier.fillMaxSize(),
        {
            TopAppBar(
                { label() },
                Modifier.shadow(12.dp),
                navigationIcon = leftAction,
                actions = rightAction,
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = 30.dp, vertical = 24.dp)
                .verticalScroll(scrollState)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content(controller)
        }
    }
}

@Composable
fun FullScreenDialog(
    label: @Composable () -> Unit = { },
    onDismiss: () -> Unit = { },
    leftAction: @Composable () -> Unit = { },
    rightAction: @Composable RowScope.() -> Unit = { },
    content: @Composable LayoutController.() -> Unit
) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Layout(label, leftAction, rightAction, content)
    }
}

@Composable
fun ConfirmationDialog(
    label: @Composable () -> Unit = { },
    onDismiss: () -> Unit = { },
    onConfirm: () -> Unit = { },
    enabled: Boolean = true,
    content: @Composable LayoutController.() -> Unit
) {
    FullScreenDialog(
        label,
        onDismiss,
        { IconButton(onDismiss) { Icon(Icons.Default.Close, "close") } },
        { TextButton({ onConfirm(); onDismiss() }, enabled = enabled) { Text("Confirm") } },
        content
    )
}

//@Composable
//fun RequestDialog(
//    label: @Composable () -> Unit = { },
//    onDismiss: () -> Unit = { },
//    responseFlow: ResponseFlow<*>,
//    content: @Composable DialogContent
//) {
//    val response by rememberAnyResponse()
//    val loading = response is Response.Loading
//
//    ConfirmationDialog(label, onDismiss) {
//        if (loading) {
//            LoadingBox()
//        }
//
//        content(it)
//    }
//}