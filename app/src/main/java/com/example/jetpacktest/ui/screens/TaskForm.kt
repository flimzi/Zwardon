package com.example.jetpacktest.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.R
import com.example.jetpacktest.data.AmountUnit
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.data.Drug
import com.example.jetpacktest.data.Task
import com.example.jetpacktest.data.TaskDrug
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.ui.ActionScreen
import com.example.jetpacktest.ui.DatePickerDialogField
import com.example.jetpacktest.ui.DropDownField
import com.example.jetpacktest.ui.DropDownListElement
import com.example.jetpacktest.ui.FullScreenDialog
import com.example.jetpacktest.ui.IntSlider
import com.example.jetpacktest.ui.LookupList
import com.example.jetpacktest.ui.ProcessingScreen
import com.example.jetpacktest.ui.SelectionList
import com.example.jetpacktest.util.Request
import com.example.jetpacktest.util.Response
import com.example.jetpacktest.util.response
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForm(
    parent: AuthenticatedUser,
    task: Task = Task(),
    onChange: (Task) -> Unit =  { },
    editing: Boolean = false,
    content: @Composable () -> Unit = { }
) {
    var taskState by remember { mutableStateOf(task) }
    LaunchedEffect(taskState) { onChange(taskState) }

    var datePickerState by remember { mutableStateOf(DatePickerState(Locale.getDefault(), taskState.start_date.toEpochMilliseconds())) }
    val timePickerState = rememberTimePickerState(taskState.startDate.hour, taskState.startDate.minute, true)

    LaunchedEffect(datePickerState, timePickerState.hour, timePickerState.minute) {
        taskState = taskState.withStartDate(taskState.start_date, timePickerState.hour, timePickerState.minute)
    }

    var recurring by remember { mutableStateOf(taskState.recurring) }

    Column {
        Request(Api.Users.getChildren(parent.accessToken)) { response ->
            val children = response.resultOrNull ?: listOf()

            DropDownField(
                children.map { DropDownListElement(it.fullName, it.id) },
                { taskState = taskState.copy(receiver_id = it) },
                taskState.receiver_id,
                !editing
            ) {
                if (response is Response.Loading)
                    LoadingIndicator()
                else
                    Text("Patient")
            }
        }

        DropDownField(
            Task.Type.entries.toTypedArray().map { DropDownListElement(it.label, it) },
            { taskState = taskState.copy(type = it.id) },
            taskState.typeId,
            !editing
        ) {
            Text("Task Type")
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TimeInput(timePickerState)
        }

        DatePickerDialogField(datePickerState) { datePickerState = it }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Lasts ${taskState.durationOrDefault} minutes")
            Spacer(Modifier.width(8.dp))

            IntSlider(5..30, taskState.durationOrDefault) {
                taskState = taskState.copy(duration_seconds = it)
            }
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Recurring")
            Spacer(Modifier.width(8.dp))

            Switch(recurring, { recurring = it })
        }

        AnimatedVisibility(
            recurring,
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Every ${taskState.interval_seconds ?: 1} days")
                Spacer(modifier = Modifier.width(8.dp))

                IntSlider(1..14, taskState.interval_seconds) {
                    taskState = taskState.copy(interval_seconds = it)
                }
            }
        }

        content()

        OutlinedTextField(
            taskState.info.orEmpty(),
            { taskState = taskState.copy(info = it) },
            Modifier.fillMaxWidth(),
            label = { Text("Task information") },
            minLines = 5
        )
    }
}

@Composable
fun TaskDrugForm(
    currentUser: AuthenticatedUser,
    task: Task = Task(),
    onChange: (Task) -> Unit = { }
) {
    val drugs = remember { mutableStateListOf<Drug>() }
    LaunchedEffect(Unit) { Api.Drugs.getForUser(currentUser.accessToken).response { drugs.addAll(it) }.collect { } }

    val taskDrugs = remember { task.taskDrugs.orEmpty().toMutableStateList() }
    LaunchedEffect(taskDrugs) { onChange(task.copy(taskDrugs = taskDrugs)) }

    var adding by remember { mutableStateOf(false) }
    var finding by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Drug?>(null) }

    val drugMap = drugs.associateBy { it.id }
    val selectedDrugs = taskDrugs.map { it.drugId }.toSet()

    Column {
        ListItem(
            { Text(stringResource(R.string.findDrug)) },
            Modifier.clickable { finding = true  },
            trailingContent = { Icon(Icons.Default.Search, "search") }
        )

        ListItem(
            { Text(stringResource(R.string.addDrug)) },
            Modifier.clickable { adding = true },
            trailingContent = { Icon(Icons.Default.Add, "add") }
        )

        SelectionList(
            taskDrugs.map { taskDrug ->
                val drug = drugMap[taskDrug.drugId] ?: Drug()
                DropDownListElement("${drug.name} (${taskDrug.amount} ${drug.unitId.short})", taskDrug)
            }
        ) {
            taskDrugs.remove(it)
        }
    }

    val currentSelected = selected
    if (currentSelected != null) {
        AmountDialog(currentSelected.unitId, onDismiss = { selected = null }) {
            taskDrugs.add(TaskDrug(drugId = currentSelected.id, amount = it))
            selected = null
        }
    }

    if (finding) {
        FullScreenDialog {
            ActionScreen(
                { Text(stringResource(R.string.findDrug)) },
                { finding = false }
            ) {
                LookupList(
                    drugs.filterNot { it.id in selectedDrugs }.map { DropDownListElement(it.name, it) },
                    { selected = it; finding = false }
                ) {
                    Text(stringResource(R.string.name))
                }
            }
        }
    }

    if (adding) {
        FullScreenDialog {
            ProcessingScreen(
                { Text(stringResource(R.string.addDrug)) },
                { adding = false },
                {
                    drugs.add(it)
                    adding = false
                    selected = it
                },
                Drug(),
                { Api.Drugs.add(currentUser.accessToken, drug = it) }
            ) { _, drug, onChange ->
                DrugForm(drug, onChange)
            }
        }
    }
}

@Composable
fun AmountField(unit: AmountUnit, amount: Int = 0, onChange: (Int) -> Unit = { }) {
    OutlinedTextField(
        amount.toString(),
        { onChange(it.toIntOrNull() ?: 0) },
        label = { Text(stringResource(R.string.amount)) },
        suffix = { Text(unit.short) }
    )
}

@Composable
fun AmountDialog(unit: AmountUnit, amount: Int = 0, onDismiss: () -> Unit = { }, onConfirm: (Int) -> Unit = { }) {
    var amountState by remember { mutableIntStateOf(amount) }

    AlertDialog(
        onDismiss,
        { TextButton({ onConfirm(amountState) }) { Text("OK") } },
        text = { AmountField(unit, amountState) { amountState = it } }
    )
}