package com.example.jetpacktest.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.jetpacktest.R
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogField(
    datePickerState: DatePickerState,
    onChange: (DatePickerState) -> Unit = { }
) {
    var dialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        SimpleDateFormat.getDateInstance().format(Date(datePickerState.selectedDateMillis!!)), { },
        Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton({ dialog = true }) {
                Icon(Icons.Default.DateRange, "Date")
            }
        }
    )

    if (dialog) {
        DatePickerDialog(
            { dialog = false },
            {
                TextButton({
                    onChange(datePickerState)
                    dialog = false
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton({
                    dialog = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(datePickerState)
        }
    }
}