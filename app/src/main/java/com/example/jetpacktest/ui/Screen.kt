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
import androidx.compose.material.icons.filled.Add
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
import com.example.jetpacktest.util.response
import kotlinx.coroutines.launch

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
fun StateScreen(
    label: @Composable () -> Unit = { },
    leftAction: @Composable () -> Unit = { },
    rightAction: @Composable RowScope.() -> Unit = { },
    initialState: ScreenState = ScreenState(),
    content: @Composable (Transform<ScreenState>) -> Unit
) {
    var screenState by remember { mutableStateOf(initialState) }
    LaunchedEffect(initialState) { screenState = initialState }

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
    fun mergeWith(response: Response<*>)
        = this.copy(
            loading = response is Response.Loading,
            message = if (response is Response.Error) response.message else message
        )

    val screenState get() = ScreenState(loading, message, fab)
}

@Composable
fun ActionScreen(
    label: @Composable () -> Unit = { },
    onCancel: (() -> Unit)? = null,
    onAction: ((Transform<ActionScreenState>) -> Unit)? = null,
    initialState: ActionScreenState = ActionScreenState(),
    content: @Composable (Transform<ActionScreenState>) -> Unit
) {
    var actionScreenState by remember { mutableStateOf(initialState) }
    LaunchedEffect(initialState) { actionScreenState = initialState } // this would be merged ideally

    StateScreen(
        label,
        { if (onCancel != null) IconButton(onCancel) { Icon(Icons.Default.Close, "Cancel") } },
        {
            if (onAction != null) {
                LoadingTextButton({ onAction { actionScreenState = it(actionScreenState) } }, actionScreenState.actionLoading, actionScreenState.actionEnabled)
                { Text(stringResource(actionScreenState.actionLabel)) }
            }
        },
        actionScreenState.screenState
    ) { content { actionScreenState = it(actionScreenState) }
    }
}

// this should use datascreen because it kinda has nothing to offer by its own besides the function
@Composable
fun <T, X> ProcessingScreen(
    label: @Composable () -> Unit = { },
    onCancel: (() -> Unit)? = null,
    onResult: (X) -> Unit = { },
    initialValue: T,
    processFlow: (T) -> ResponseFlow<X>? = { null },
    initialState: ActionScreenState = ActionScreenState(),
    content: @Composable (Transform<ActionScreenState>, T, (T) -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var data by remember { mutableStateOf(initialValue) }

    ActionScreen(
        label, onCancel,
        { state ->
            coroutineScope.launch {
                processFlow(data)
                    ?.response(onResult = onResult)
                    ?.collect { response ->
                        state {
                            it.copy(
                                actionLoading = response is Response.Loading,
                                message = response.messageOrNull
                            )
                        }
                    }
            }
        },
        initialState
    ) {
        content(it, data) { data = it }
    }
}

@Composable
fun <T, X> FlowScreen(
    label: @Composable () -> Unit = { },
    onCancel: (() -> Unit)? = null,
    onResult: (X) -> Unit = { },
    inputFlow: ResponseFlow<T>,
    initialValue: T,
    processFlow: (T) -> ResponseFlow<X>? = { null },
    initialState: ActionScreenState = ActionScreenState(),
    content: @Composable (Transform<ActionScreenState>, T, (T) -> Unit) -> Unit
) {
    Request(inputFlow) { response ->
        ProcessingScreen(label, onCancel, onResult, initialValue, processFlow, initialState.mergeWith(response), content)
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
fun AddActionButton(
    text: @Composable () -> Unit = { Text(stringResource(R.string.add)) },
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        text,
        { Icon(Icons.Default.Add, contentDescription = "Add") },
        onClick,
    )
}

@Composable
fun AddTaskActionButton(onClick: () -> Unit) {
    AddActionButton({ Text(stringResource(R.string.addTask)) }, onClick)
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