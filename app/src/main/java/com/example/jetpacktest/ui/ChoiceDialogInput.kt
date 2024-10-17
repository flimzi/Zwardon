package com.example.jetpacktest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastJoinToString
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

open class Choice<T>(
    val name: String,
    val value: T,
    var chosen: MutableState<Boolean> = mutableStateOf(false),
)

// no time for reworking this
@Composable
fun <T> ChoiceDialogInput(
    choices: List<Choice<T>>,
    onChange: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    maxChosen: Int = choices.size,
    minChosen: Int = 0,
    enabled: Boolean = true,
    launch: Boolean = false,
    label: @Composable () -> Unit = { }
) {
    var visible by rememberSaveable { mutableStateOf(launch) }
    var error by rememberSaveable { mutableStateOf("") }
    fun chosen() = choices.filter { it.chosen.value }
    val maxChosenActual = min(choices.size, max(chosen().size, maxChosen))

    fun change() {
        if (chosen().size < minChosen)
            error = "Select at least $minChosen"

        onChange(chosen().map { it.value })
        visible = false
    }

    fun choose(choice: Choice<T>) {
        if (choice.chosen.value) {
            if (chosen().size <= minChosen)
                return

            choice.chosen.value = false
        } else {
            if (chosen().size >= maxChosenActual) {
                if (chosen().isEmpty())
                    return

                chosen()[Random.nextInt(chosen().size)].chosen.value = false
            }

            choice.chosen.value = true

            if (maxChosenActual == 1)
                change()
        }
    }

    OutlinedTextField(
        chosen().map { it.name }.fastJoinToString(), { }, modifier,
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
                                .fillMaxWidth(),
//                                .clickable { choose(choice) },
                            Arrangement.SpaceBetween, Alignment.CenterVertically
                        ) {
                            Text(choice.name)
                            Checkbox(choice.chosen.value, { choose(choice) })
                        }
                    }
                }
            }
        )
    }
}