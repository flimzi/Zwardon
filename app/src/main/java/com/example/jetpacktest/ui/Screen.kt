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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.jetpacktest.util.LoadingTextButton
import com.example.jetpacktest.util.Response
import com.example.jetpacktest.util.ResponseFlow
import com.example.jetpacktest.util.onError
import com.example.jetpacktest.util.onResult
import com.example.jetpacktest.util.rememberAnyResponse
import kotlinx.coroutines.launch

// probably need to change response to loading and error booleans
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    label: @Composable () -> Unit = { },
    response: Response<*> = Response.Idle,
    leftAction: @Composable () -> Unit = { },
    rightAction: @Composable RowScope.() -> Unit = { },
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    if (response is Response.Loading) {
        LoadingBox(true)
    }

    if (response is Response.Error) {
        LaunchedEffect(response) {
            snackbarHostState.showSnackbar(response.message)
        }
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
            content()
        }
    }
}

@Composable
fun FullScreenDialog(
    onDismiss: () -> Unit = { },
    content: @Composable () -> Unit
) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false), content)
}

@Composable
fun ConfirmationScreen(
    label: @Composable () -> Unit = { },
    response: Response<*> = Response.Idle,
    onDismiss: () -> Unit = { },
    onConfirm: () -> Unit = { },
    confirmationLoading: Boolean = false,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Screen(
        label, response,
        { IconButton(onDismiss) { Icon(Icons.Default.Close, "close") } },
        { LoadingTextButton(onConfirm, confirmationLoading, enabled) { Text("Confirm") } },
        content
    )
}

@Composable
fun <T> ProcessingScreen(
    label: @Composable () -> Unit = { },
    onDismiss: () -> Unit = { },
    input: ResponseFlow<T>,
    process: (T) -> ResponseFlow<*>,
    enabler: (T) -> Boolean,
    content: @Composable (T?, (T) -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var processingData by remember { mutableStateOf<T?>(null) }
    var screenState by rememberAnyResponse()
    var confirmationLoading by remember { mutableStateOf(false) }

    val currentData = processingData
    val enabled = currentData != null && enabler(currentData)

    LaunchedEffect(Unit) {
        input.collect {
            screenState = it

            if (it is Response.Result) {
                processingData = it.result
            }
        }
    }

    fun onConfirm() {
        coroutineScope.launch {
            process(currentData!!).apply {
                onResult { onDismiss() }
                onError { screenState = Response.Error(it) }
                collect { confirmationLoading = it is Response.Loading }
            }
        }
    }

    ConfirmationScreen(label, screenState, onDismiss, ::onConfirm, confirmationLoading, enabled) {
        content(processingData) { processingData = it }
    }
}