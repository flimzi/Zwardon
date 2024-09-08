package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Task
import com.example.jetpacktest.data.TaskViewModel
import com.example.jetpacktest.ui.MyDatePicker
import com.example.jetpacktest.ui.MyTimePicker
import com.example.jetpacktest.util.Response
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
    taskId: Int? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var updateState by remember { mutableStateOf<Response<Int>>(Response.Idle) }
    var task by remember { mutableStateOf<Task?>(null) }
    val taskState by taskViewModel.get(taskId).collectAsState(Response.Idle)
    val enabled by remember { mutableStateOf(true) }

    val taskDate = task?.start_date?.toLocalDateTime(TimeZone.currentSystemDefault())
    val nowDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val startDate = remember { mutableStateOf<LocalDate?>(taskDate?.date ?: LocalDate(nowDate.year, nowDate.month, nowDate.dayOfMonth)) }
    val startTime = remember { mutableStateOf(taskDate?.time ?: LocalTime(nowDate.time.hour, nowDate.time.minute)) }

    var info by remember { mutableStateOf(task?.info ?: "") }

    fun cancel() = navController.popBackStack()
    fun save() {
        val startInstant = LocalDateTime(startDate.value!!, startTime.value).toInstant(TimeZone.currentSystemDefault())
        val newTask = Task(0, 501, 0, 0, 0, info, startInstant)

        coroutineScope.launch {
            taskViewModel.add(userId, newTask).collect { updateState = it }
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
            Column(Modifier.padding(it)) {
                MyDatePicker(startDate, true, enabled)

                MyTimePicker(startTime, enabled)

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