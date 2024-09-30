package com.example.jetpacktest.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.data.Drug
import com.example.jetpacktest.data.TaskDrug
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.ui.CertainNumberInput
import com.example.jetpacktest.ui.Choice
import com.example.jetpacktest.ui.SelectionDialogInput
import com.example.jetpacktest.util.Response
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun TaskDrugSelectionInput(
    currentUser: AuthenticatedUser,
    userId: Int,
    taskDrugs: MutableList<TaskDrug> = mutableListOf(),
    onChange: (List<TaskDrug>) -> Unit = {},
) {
    val drugs = remember { mutableStateListOf<Drug>() }
    var showAddDrugDialog by remember { mutableStateOf(false) }
    var showAmountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Api.Drugs.get(currentUser.accessToken, userId).filterIsInstance<Response.Result<List<Drug>>>().collect {
            drugs.addAll(it.result)
        }
    }

    SelectionDialogInput(
        drugs.map { Choice(it.name, it) }.filter { !it.chosen.value }, // not sure if this works
        onSelect = { showAmountDialog = true },
        onAdd = { showAddDrugDialog = true }
    )

    if (showAmountDialog) {
        TaskDrugAmountDialog(onDismiss = { showAmountDialog = false }) {
            taskDrugs.add(it)
            onChange(taskDrugs)
        }
    }

    if (showAddDrugDialog) {
        AddDrugDialog(
            currentUser, userId,
            onDismiss = { showAddDrugDialog = false },
            onAdded = { },
        )
    }
}

@Composable
fun TaskDrugAmountDialog(
    taskDrug: TaskDrug = TaskDrug(),
    onDismiss: () -> Unit,
    onConfirm: (TaskDrug) -> Unit
) {
    var amount by remember { mutableDoubleStateOf(taskDrug.amount.toDouble()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton({
                amount.let {
                    if (it > 0) {
                        onConfirm(taskDrug.copy(amount = it.toInt()))
                    }
                }

                onDismiss()
            }) {
                Text("OK")
            }
        },
        text = {
            CertainNumberInput(amount, { amount = it }) {
                Text("Amount")
            }
        }
    )
}