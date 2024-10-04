package com.example.jetpacktest.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.jetpacktest.R
import com.example.jetpacktest.ui.screens.LoadingScreen
import com.example.jetpacktest.util.LoadingTextButton
import com.example.jetpacktest.util.Request
import com.example.jetpacktest.util.Response
import com.example.jetpacktest.util.ResponseFlow
import com.example.jetpacktest.util.rememberAnyResponse
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
    fab: @Composable () -> Unit = { },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = fab
    ) {
        Box(Modifier.padding(it)) {
            content()
        }
    }
}

data class ScreenState(
    val loading: Boolean = false,
    val message: String? = null,
    val fab: @Composable () -> Unit = { }
) {
    fun mergeWith(response: Response<*>) = this.copy(loading = response is Response.Loading, message = response.messageOrNull)
}

typealias Transform <T> = ((T) -> T) -> Unit

@Composable
fun Screen2(
    label: @Composable () -> Unit = { },
    leftAction: @Composable () -> Unit = { },
    rightAction: @Composable RowScope.() -> Unit = { },
    content: @Composable (Transform<ScreenState>) -> Unit
) {
    var screenState by remember { mutableStateOf(ScreenState()) }

    Screen(label, leftAction, rightAction, screenState.loading, screenState.message, screenState.fab) {
        content { transformState -> screenState = transformState(screenState) }
    }
}

data class ActionScreenState(
    val loading: Boolean = false,
    val message: String? = null,
    val fab: @Composable () -> Unit = { },
    @StringRes val actionLabel: Int = R.string.save,
    val actionLoading: Boolean = false,
    val actionEnabled: Boolean = true,
) {
    fun mergeWith(response: Response<*>) = this.copy(loading = response is Response.Loading, message = response.messageOrNull)
    val screenState get() = ScreenState(loading, message, fab)
}

@Composable
fun ActionScreen(
    label: @Composable () -> Unit = { },
    onCancel: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    content: @Composable (Transform<ActionScreenState>) -> Unit
) {
    var actionScreenState by remember { mutableStateOf(ActionScreenState()) }

    Screen2(
        label,
        { if (onCancel != null) IconButton(onCancel) { Icon(Icons.Default.Close, "Cancel") } },
        { if (onAction != null) LoadingTextButton(onAction, actionScreenState.actionLoading, actionScreenState.actionEnabled) { Text("Save") } },
    ) { screenState ->
        content { transformState ->
            actionScreenState = transformState(actionScreenState)
            screenState { actionScreenState.screenState }
        }
    }
}

@Composable
fun EditActionButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        { Text(stringResource(R.string.edit)) },
        { Icon(Icons.Default.Edit, contentDescription = "Edit") },
        onClick,
    )
}

@Composable
fun AddActionButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        { Text(stringResource(R.string.add)) },
        { Icon(Icons.Default.Edit, contentDescription = "Edit") },
        onClick,
    )
}

@Composable
fun Screen(
    label: @Composable () -> Unit = { },
    leftAction: @Composable () -> Unit = { },
    rightAction: @Composable RowScope.() -> Unit = { },
    state: Response<*> = Response.Idle,
    content: @Composable () -> Unit
) {
    Screen(label, leftAction, rightAction, state is Response.Loading, state.messageOrNull, {}, content)
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
fun Header(@StringRes id: Int) {
    Text(stringResource(id), fontSize = 24.sp, fontStyle = FontStyle.Italic)
    HorizontalDivider()
    Spacer(Modifier.height(16.dp))
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

typealias StateController<T> = ((Response<T>) -> Unit)

@Composable
fun <T> ProcessingScreen(
    label: @Composable () -> Unit = { },
    onBack: (() -> Unit)? = null,
    onConfirm: (T, StateController<*>) -> Unit  = { _, _ -> },
    state: Response<T>,
    confirmationEnabler: (T) -> Boolean = { true },
    content: @Composable (StateController<T>) -> Unit
) {
    var responseState by rememberResponse<T>()
    LaunchedEffect(state) { responseState = state }

    var processingState by rememberAnyResponse()

    var data by remember { mutableStateOf<T?>(null) }
    LaunchedEffect(responseState) { if (responseState is Response.Result) data = responseState.resultOrNull }
    val currentData = data

    val screenState by remember {
        derivedStateOf {
            when {
                responseState is Response.Error || responseState is Response.Loading -> responseState
                processingState is Response.Error -> processingState

                else -> Response.Idle
            }
        }
    }

    ConfirmationScreen(
        label,
        onBack,
        { if (currentData != null) onConfirm(currentData) { processingState = it } },
        screenState,
        processingState is Response.Loading,
        currentData != null && confirmationEnabler(currentData)
    ) {
        content { responseState = it }
    }
}

@Composable
fun <T, X> FlowScreen(
    label: @Composable () -> Unit = { },
    onBack: (() -> Unit)? = null,
    onResult: (X) -> Unit = { },
    input: ResponseFlow<T>,
    process: (T) -> ResponseFlow<X>,
    confirmationEnabler: (T) -> Boolean = { true },
    content: @Composable (T?, StateController<T>) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Request(input) { response ->
        ProcessingScreen(
            label,
            onBack,
            { value, onState -> coroutineScope.launch { process(value).response(onResult = onResult).collect(onState) } },
            response,
            confirmationEnabler
        ) { onState ->
            content(response.resultOrNull, onState)
        }
    }
}

// mayhaps
@Composable
fun <T> State(value: T, content: @Composable (T, (T) -> Unit) -> Unit) {
    var state by remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        state = value
    }
    content(state) {
        state = it
    }
}

