package com.example.jetpacktest.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePicker(
    date: MutableState<LocalDate?>,
    required: Boolean = false,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = { Text("Select date") }
) {
    var visible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val selectedDate = datePickerState.selectedDateMillis?.let {
        SimpleDateFormat.getDateInstance().format(Date(it))
    }.orEmpty()

    fun show() { visible = enabled }

    fun hide() {
        visible = false
        error = required && date.value == null
    }

    fun select() {
        date.value = datePickerState.selectedDateMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
        }

        hide()
    }

    LaunchedEffect(date) {
        datePickerState.selectedDateMillis = date.value?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds()
    }

    OutlinedTextField(
        selectedDate, { },
        Modifier.fillMaxWidth(),
        label = label,
        readOnly = true,
        isError = error,
        trailingIcon = {
            IconButton(::show) {
                Icon(Icons.Default.DateRange, "Select date")
            }
        }
    )

    if (visible) {
        DatePickerDialog(
            onDismissRequest = ::hide,
            confirmButton = {
                TextButton(::select) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(::hide) {
                    Text("Cancel")
                }
            },
        ) { DatePicker(datePickerState, showModeToggle = false) }
    }
}