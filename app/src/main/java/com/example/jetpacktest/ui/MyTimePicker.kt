package com.example.jetpacktest.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalTime

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePicker(
    time: MutableState<LocalTime>,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = { Text("Select time") }
) {
    val state = rememberTimePickerState(time.value.hour, time.value.minute)
    var visible by remember { mutableStateOf(false) }

    fun show() { visible = enabled }
    fun hide() { visible = false }

    fun select() {
        hide()
        time.value = LocalTime(state.hour, state.minute)
    }

    OutlinedTextField(
        time.value.toString(), { },
        Modifier.fillMaxWidth(),
        label = label,
        readOnly = true,
        trailingIcon = {
            IconButton(::show) {
                Icon(Icons.Default.DateRange, "Select time")
            }
        }
    )

    if (visible) {
        AlertDialog(
            ::hide,
            { TextButton(::select) { Text("OK") } },
            text = { TimePicker(state) }
        )
    }
}