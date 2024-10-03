package com.example.jetpacktest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.jetpacktest.ui.screens.LoadingScreen
import com.example.jetpacktest.util.LoadingTextButton
import com.example.jetpacktest.util.Response
import com.example.jetpacktest.util.ResponseFlow
import com.example.jetpacktest.util.rememberResponse
import com.example.jetpacktest.util.response
import kotlinx.coroutines.launch

// probably need to change response to loading and error booleans
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    label: @Composable () -> Unit = { },
    leftAction: @Composable () -> Unit = { },
    rightAction: @Composable RowScope.() -> Unit = { },
    loading: Boolean = false,
    message: String? = null,
    content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    if (loading) {
        LoadingScreen(true)
    }

    if (message != null) {
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
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
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior() // does this do anything
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Box(Modifier.padding(it)) {
            content()
        }
    }
}

@Composable
fun Screen(
    label: @Composable () -> Unit = { },
    leftAction: @Composable () -> Unit = { },
    rightAction: @Composable RowScope.() -> Unit = { },
    state: Response<*> = Response.Idle,
    content: @Composable () -> Unit
) {
    Screen(label, leftAction, rightAction, state is Response.Loading, state.messageOrNull, content)
}

@Composable
fun Scrollable(content: @Composable () -> Unit) {
    val scrollState = rememberScrollState()

    Box(Modifier.verticalScroll(scrollState)) {
        content()
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
fun Content(content: @Composable () -> Unit) {
    Scrollable {
        Column(
            Modifier
                .padding(horizontal = 30.dp, vertical = 24.dp)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun ConfirmationScreen(
    label: @Composable () -> Unit = { },
    onBack: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    state: Response<*> = Response.Idle,
    confirmationLoading: Boolean = false,
    confirmationEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Screen(
        label,
        { if (onBack != null) IconButton(onBack) { Icon(Icons.AutoMirrored.Default.ArrowBack, "Back") } },
        { if (onConfirm != null) LoadingTextButton(onConfirm, confirmationLoading, confirmationEnabled) { Text("Confirm") } },
        state, content
    )
}

@Composable
fun <T, X> ResponseScreen(
    label: @Composable () -> Unit = { },
    onDismiss: () -> Unit = { },
    onSuccess: (X) -> Unit = { },
    // this could be simplified to just Response<T> but LaunchedEffect(inpuState) would need to stay because for updating the data state
    // so do that first and build ProcessingScreen on top
    inputFlow: ResponseFlow<T>,
    // this could be simplified to a regular callback because a handle for onMessage is already present
    // and there would need to be added a handler for confirmationloading (as well as probably enabled)
    processingFlow: (T) -> ResponseFlow<X>,
    confirmationEnabler: (T) -> Boolean,
    content: @Composable (T?, (T) -> Unit, (String) -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var inputState by rememberResponse<T>()
    var processingState by rememberResponse<X>()

    val screenState by remember {
        derivedStateOf {
            when {
                inputState is Response.Error || inputState is Response.Loading -> inputState
                processingState is Response.Error -> processingState

                else -> Response.Idle
            }
        }
    }

    var data by remember { mutableStateOf<T?>(null) }
    val currentData = data

    LaunchedEffect(Unit) {
        inputFlow.response { data = it }.collect { inputState = it }
    }

    ConfirmationScreen(
        label, onDismiss,
        {
            if (currentData != null) {
                coroutineScope.launch {
                    processingFlow(currentData).response { onSuccess(it) }.collect { processingState = it }
                }
            }
        },
        screenState,
        processingState is Response.Loading,
        currentData != null && confirmationEnabler(currentData)
    ) {
        content(currentData, { data = it }, { processingState = Response.Error(it) })
    }
}