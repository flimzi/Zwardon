package com.example.jetpacktest.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.Task
import com.example.jetpacktest.util.Response
import io.ktor.client.call.body
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(appNavController: NavHostController, homeNavController: NavController, authViewModel: AuthViewModel) {
    var upcomingTasks by remember { mutableStateOf<Response<List<Task>>>(Response.Idle)}

    LaunchedEffect(Unit) {
        upcomingTasks = Response.Loading
        val result = Api.Events.getUpcomingTasks(authViewModel.accessToken.value!!).body<List<Task>>()
        upcomingTasks = Response.Result(result)
    }

    when (val state = upcomingTasks) {
        is Response.Loading -> {
            Column(Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
        is Response.Result<List<Task>> -> {
            LazyColumn(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.result.groupBy { it.startDate.date }.forEach { (date, tasks) ->
                    stickyHeader {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(date.dayOfWeek.name, style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
                            Text(date.toJavaLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        }
                    }

                    items(tasks, key = { it.id }) {
                        var expanded by remember { mutableStateOf(false) }

                        Card(
                            { expanded = !expanded },
                            Modifier.animateContentSize()
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(it.typeId), style = TextStyle(fontWeight = FontWeight.Bold))
                                    Text(it.startDate.time.toString())
                                }
                            }

                            if (expanded && it.info?.isBlank() == false) {
                                Text(it.info.toString(), Modifier.padding(8.dp))
                            }
                        }
                    }
                }
            }
        }
        else -> { }
    }
}