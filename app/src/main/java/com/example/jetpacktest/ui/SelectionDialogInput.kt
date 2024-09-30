package com.example.jetpacktest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun <T> SelectionDialogInput(
    choices: List<Choice<T>>,
    modifier: Modifier = Modifier,
    onSelect: (Choice<T>) -> Unit = { it.chosen.value = !it.chosen.value },
    onAdd: () -> Unit = { },
    launch: Boolean = false,
    label: @Composable () -> Unit = { }
) {
    var dialogVisible by rememberSaveable { mutableStateOf(launch) }
    val scrollState = rememberScrollState()

    fun select(item: Choice<T>) {
        onSelect(item)
        dialogVisible = false
    }

    Box(
        Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .height(42.dp)
    ) {
        SelectionList(
            choices.filter { it.chosen.value },
            ::select,
            { dialogVisible = true },
            { Text(it.name) },
            { Icon(Icons.Default.Clear, "Remove") }
        )
    }

    if (dialogVisible) {
        AlertDialog({ dialogVisible = false }, { },
            title = label,
            text = {
                SelectionList(
                    choices.filter { !it.chosen.value },
                    ::select,
                    onAdd,
                    { Text(it.name) },
                    { Icon(Icons.Default.Add, "Add") }
                )
            }
        )
    }
}

@Composable
private fun <T> SelectionList(
    choices: List<Choice<T>>,
    onSelect: (Choice<T>) -> Unit,
    onAdd: () -> Unit,
    headlineContent: @Composable (Choice<T>) -> Unit,
    icon:  @Composable () -> Unit,
) {
    LazyColumn {
        item {
            Button(
                onAdd,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Icon(Icons.Default.AddCircle, "Add")
                Text("Add")
            }
        }

        items(choices) {
            ListItem(
                headlineContent = { headlineContent(it) },
                trailingContent = {
                    IconButton({ onSelect(it) }, content = icon)
                }
            )
        }
    }
}