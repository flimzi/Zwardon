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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialogInput(
    date: LocalDate?,
    onChange: (LocalDate?) -> Unit = { },
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
        error = required && date == null
    }

    fun select() {
        val selected = datePickerState.selectedDateMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
        }

        onChange(selected)
        hide()
    }

    LaunchedEffect(date) {
        datePickerState.selectedDateMillis = date?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds()
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

@Composable
fun CertainDateDialogInput(
    date: LocalDate,
    onChange: (LocalDate) -> Unit = { },
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = { Text("Select date") }
) {
    DateDialogInput(date, { onChange(it ?: date) }, true, enabled, label)
}

// this should be the default and not the overcomplicated localdatetime
@Composable
fun DateDialogInput(
    instant: Instant? = null,
    onChange: (Instant) -> Unit = { },
    label: @Composable (() -> Unit)? = { Text("Select date") }
) {
    val dateTime = (instant ?: Clock.System.now()).toLocalDateTime(TimeZone.currentSystemDefault())
    val date by remember {
        mutableStateOf(LocalDate(dateTime.year, dateTime.month, dateTime.dayOfMonth))
    }

    DateDialogInput(
        date,
        { if (it != null) onChange(it.atTime(0, 0).toInstant(TimeZone.currentSystemDefault())) },
        label = label
    )
}