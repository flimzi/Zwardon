package com.example.jetpacktest.ui

import android.app.AlertDialog
import android.graphics.Outline
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.util.fastJoinToString
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class Choice<T>(val name: String, val value: T, var selected: MutableState<Boolean> = mutableStateOf(false))

@Composable
fun <T> ChoiceDialogInput(
    choices: List<Choice<T>>,
    onChange: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    maxSelected: Int = choices.size,
    minSelected: Int = 0,
    enabled: Boolean = true,
    launch: Boolean = false,
    label: @Composable () -> Unit = { }
) {
    var visible by rememberSaveable { mutableStateOf(launch) }
    var error by rememberSaveable { mutableStateOf("") }
    val selected = choices.filter { it.selected.value }
    val maxSelectedActual = min(choices.size, max(selected.size, maxSelected))

    fun change() {
        if (selected.size < minSelected)
            error = "Select at least $minSelected"

        onChange(selected.map { it.value })
        visible = false
    }

    fun select(choice: Choice<T>) {
        if (choice.selected.value) {
            if (selected.size <= minSelected)
                return

            choice.selected.value = false
        } else {
            if (selected.size >= maxSelectedActual) {
                if (selected.isEmpty())
                    return

                selected[Random.nextInt(selected.size)].selected.value = false
            }

            choice.selected.value = true

            if (maxSelectedActual == 1)
                change()
        }
    }

    OutlinedTextField(
        selected.map { it.name }.fastJoinToString(), { }, modifier,
        label = label,
        readOnly = true,
        enabled = choices.isNotEmpty(),
        trailingIcon = {
            IconButton({ if (enabled) visible = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Select")
            }
        },
        isError = error.isNotEmpty(),
        supportingText = { if (error.isNotEmpty()) Text(error) }
    )

    if (visible) {
        AlertDialog(
            ::change, {  },
            title = label,
            text = {
                Column {
                    choices.forEach { choice ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { select(choice) },
                            Arrangement.SpaceBetween, Alignment.CenterVertically
                        ) {
                            Text(choice.name)
                            Checkbox(choice.selected.value, { select(choice) })
                        }
                    }
                }
            }
        )
    }
}
