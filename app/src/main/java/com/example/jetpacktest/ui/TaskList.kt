package com.example.jetpacktest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jetpacktest.data.Task
import com.example.jetpacktest.data.User

@Composable
fun TaskList(
    tasks: List<Task> = listOf(),
    // this is not going to work for now because there is no endpoint for children tasks
    // i mean there is but its not in this app
    userInformation: ((Int) -> User)? = null,
    onClick: (Task) -> Unit = { }
) {
    Column(Modifier.fillMaxWidth()) {
        tasks.groupBy { it.startDate.date }.forEach { (date, tasks) ->
            Row {
                Text("${date.dayOfWeek} $date")
            }

            HorizontalDivider()

            tasks.forEach { task ->
                ListItem(
                    { Text(task.typeId.label) },
                    Modifier.fillMaxWidth().clickable { onClick(task) },
                    supportingContent = { Text(task.info.orEmpty()) },
                    leadingContent = { Text(task.startDate.time.toString()) },
                    trailingContent = userInformation?.invoke(task.receiver_id)?.let {
                        {
                            Row {
                                Icon(Icons.Default.AccountCircle, "user")
                                Text(it.fullName)
                            }
                        }
                    }
                )
            }
        }
    }
}