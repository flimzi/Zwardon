package com.example.jetpacktest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.Task
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.ui.MyDatePicker
import com.example.jetpacktest.ui.MyTimePicker
import com.example.jetpacktest.util.Response
import io.ktor.client.call.body
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, authViewModel: AuthViewModel, userId: Int, task: Task? = null) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var readOnly by remember { mutableStateOf(false) }
    var updateState by remember { mutableStateOf<Response<Int>>(Response.Idle) }
    var info by remember { mutableStateOf(task?.info ?: "") }

    val taskDate = task?.start_date?.toLocalDateTime(TimeZone.currentSystemDefault())
    val nowDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val startDate = remember { mutableStateOf(taskDate?.date) }
    val startTime = remember { mutableStateOf(taskDate?.time ?: LocalTime(nowDate.time.hour, nowDate.time.minute)) }

    LaunchedEffect(updateState) {
        readOnly = updateState is Response.Loading
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (task != null)
            Text("edit task ${task.id}")
        else
            Text("add task")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            info, { info = it }, Modifier.fillMaxWidth(),
            label = { Text("Task information") },
            readOnly = readOnly,
            minLines = 5
        )

        Button({
            if (startDate.value == null) {
                updateState = Response.Error("set start date")
                return@Button
            }

            val startInstant = LocalDateTime(startDate.value!!, startTime.value).toInstant(TimeZone.currentSystemDefault())
            val newTask = Task(0, 501, 0, 0, 0, info, startInstant)

            coroutineScope.launch {
                updateState = try {
                    val taskId = Api.Events.add(authViewModel.accessToken.value.orEmpty(), userId, newTask).body<Int>()
                    Response.Result(taskId)
                } catch (e : Exception) {
                    Response.ServerError
                }
            }
        },
            Modifier.fillMaxWidth(),
            !readOnly
        ) {
            if (updateState is Response.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                if (task == null)
                    Text(stringResource(Screen.AddTask(userId).name))
                else
                    Text("Edit")
            }
        }
    }

    LaunchedEffect(updateState) {
        when (val currentState = updateState) {
            is Response.Error -> Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            is Response.Result<Int> -> {
                navController.navigate(Screen.AddTask(userId).route)
                Toast.makeText(context, "Task created (${currentState.result})", Toast.LENGTH_LONG).show()
            }
            else -> { }
        }
    }
}
