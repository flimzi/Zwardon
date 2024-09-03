package com.example.jetpacktest.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.jetpacktest.R

@Composable
fun TextInput(
    @StringRes id: Int,
    text: String,
    onValueChange: (String) -> Unit,
    required: Boolean = false,
    readOnly: Boolean = false,
    hide: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
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
        label = { Text(stringResource(id)) },
        readOnly = readOnly, singleLine = true, isError = blankError,
        supportingText = { if (blankError) Text("${stringResource(id)} cannot be empty") },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
    )
}

@Composable
fun CharacterTextInput(
    @StringRes id: Int,
    text: MutableState<String>,
    required: Boolean = false,
    readOnly: Boolean = false,
) {
    TextInput(
        id, text.value,
        { input -> text.value = input.filter { it.isLetter() } },
        required, readOnly, false,
        KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}