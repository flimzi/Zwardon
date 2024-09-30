package com.example.jetpacktest.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TextInput(
    text: String,
    onValueChange: (String) -> Unit,
    required: Boolean = false,
    readOnly: Boolean = false,
    hide: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    label: @Composable () -> Unit = { }
) {
    var blankError by rememberSaveable { mutableStateOf(false) }
    var focused by rememberSaveable { mutableStateOf(false) }
    val visualTransformation = if (hide) PasswordVisualTransformation() else VisualTransformation.None

    OutlinedTextField(
        text, onValueChange,
        Modifier
            .fillMaxWidth()
            .onFocusChanged {
                blankError = focused && required && text.isBlank()
                focused = it.isFocused
            },
        label = label,
        readOnly = readOnly, singleLine = true, isError = blankError,
        supportingText = { if (blankError) Text("cannot be empty") },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
    )
}

@Composable
fun CharacterTextInput(
    text: String?,
    onValueChange: (String) -> Unit = { },
    required: Boolean = false,
    readOnly: Boolean = false,
    label: @Composable () -> Unit = { }
) {
    TextInput(
        text.orEmpty(),
        { input -> onValueChange(input.filter { it.isLetter() }) },
        required, readOnly, false,
        KeyboardOptions(keyboardType = KeyboardType.Text),
        label
    )
}

@Composable
fun NumberInput(
    number: Double? = .0,
    onChange: (Double?) -> Unit = { },
    label: @Composable () -> Unit = { }
) {
    TextInput(
        number.toString(),
        { onChange(it.toDoubleOrNull()) }
    ) { label() }
}

@Composable
fun CertainNumberInput(
    number: Double = .0,
    onChange: (Double) -> Unit = { },
    label: @Composable () -> Unit = { }
) {
    NumberInput(number, { onChange(it ?: .0) }) { label() }
}