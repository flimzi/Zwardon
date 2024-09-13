package com.example.jetpacktest.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.Task
import com.example.jetpacktest.data.TaskViewModel
import com.example.jetpacktest.data.User
import com.example.jetpacktest.ui.CertainDateDialogInput
import com.example.jetpacktest.ui.Choice
import com.example.jetpacktest.ui.ChoiceDialogInput
import com.example.jetpacktest.util.Response
import io.ktor.client.call.body
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    navController: NavController,
    authViewModel: AuthViewModel,
    taskViewModel: TaskViewModel,
    userId: Int,
    type: Task.Type = Task.Type.INFO,
    taskId: Int? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var updateState by remember { mutableStateOf<Response<Int>>(Response.Idle) }
    var task by remember { mutableStateOf<Task?>(null) }
    val taskState by taskViewModel.get(taskId).collectAsState(Response.Idle)
    val enabled by remember { mutableStateOf(true) }

    val taskDate = (task?.start_date ?: Clock.System.now()).toLocalDateTime(TimeZone.currentSystemDefault())
    var startDate by remember { mutableStateOf(LocalDate(taskDate.year, taskDate.month, taskDate.dayOfMonth)) }
    val startTime = rememberTimePickerState(taskDate.hour, taskDate.minute)
    var duration by rememberSaveable { mutableFloatStateOf(task?.duration_seconds?.toFloat() ?: 5f) }
    var recurring by rememberSaveable { mutableStateOf(false) }
    var interval by rememberSaveable { mutableFloatStateOf(1f) }
    val children = remember { mutableStateListOf<User>() }
    var receiverId by remember { mutableIntStateOf(userId) }
    var typeId by remember { mutableStateOf(type) }

    var info by remember { mutableStateOf(task?.info ?: "") }
    val scrollState = rememberScrollState()

    fun cancel() = navController.popBackStack()
    fun save() {
        val startInstant = LocalDateTime(startDate, LocalTime(startTime.hour, startTime.minute)).toInstant(TimeZone.currentSystemDefault())
        val newTask = Task(0, 501, 0, 0, 0, info, startInstant, duration.toInt(), if (recurring) interval.toInt() else null)

        coroutineScope.launch {
            taskViewModel.add(receiverId, newTask).collect { updateState = it }
        }
    }

    LaunchedEffect(Unit) {
        Api.Users.getSecondary(authViewModel.accessToken).body<List<User>>().let {
            children.addAll(it)
        }
    }

    LaunchedEffect(taskState) {
        when (val current = taskState) {
            is Response.Result<Task> -> {
                task = current.result
            }
            else -> { }
        }
    }

    Dialog(::cancel, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    { Text("Task") },
                    Modifier.shadow(12.dp),
                    navigationIcon = {
                        IconButton(::cancel) {
                            Icon(Icons.Filled.Close, "close")
                        }
                    },
                    actions = {
                        TextButton(::save, enabled = enabled) {
                            if (updateState is Response.Loading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            } else {
                                Text("Save")
                            }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                Modifier
                    .padding(it)
                    .padding(horizontal = 12.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChoiceDialogInput(
                    children.map { Choice(it.fullName, it, mutableStateOf(it.id == userId)) },
                    { receiverId = it.first().id },
                    Modifier.fillMaxWidth(),
                    minSelected = 1, maxSelected = 1,
                    enabled = enabled && task == null
                ) {
                    Text("Patient")
                }

                ChoiceDialogInput(
                    Task.Type.entries.toTypedArray().map { Choice(it.label, it, mutableStateOf(it == type)) },
                    { typeId = it.first() },
                    Modifier.fillMaxWidth(),
                    minSelected = 1, maxSelected = 1,
                    enabled = enabled && task == null
                ) {
                    Text("Task type")
                }

                when (typeId) {
                    Task.Type.DRUG -> {
                        // adddrugdialog (new or existing)
                        // list drugs
                    }
                    else -> { }
                }

                TimeInput(startTime)
                CertainDateDialogInput(startDate, { startDate = it }, enabled)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lasts ${duration.toInt()} minutes")
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(duration, onValueChange = { duration = it }, valueRange = 5f..30f, steps = 23, enabled = enabled)
                }

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recurring")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(recurring, { recurring = it }, enabled = enabled)
                }

                AnimatedVisibility(
                    recurring,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Every ${interval.toInt()} days")
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(interval, onValueChange = { interval = it }, valueRange = 1f..14f, steps = 12, enabled = enabled)
                    }
                }

                OutlinedTextField(
                    info, { info = it }, Modifier.fillMaxWidth(),
                    label = { Text("Task information") },
                    enabled = enabled,
                    minLines = 5
                )
            }

            if (taskState is Response.Loading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        LaunchedEffect(updateState) {
            when (val state = updateState) {
                is Response.Error -> snackbarHostState.showSnackbar(state.message)
                is Response.Result -> {
                    snackbarHostState.showSnackbar("Success")
                    task = null
                }
                else -> { }
            }
        }
    }
}