package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.jetpacktest.R
import com.example.jetpacktest.data.User
import com.example.jetpacktest.ui.CharacterTextInput
import com.example.jetpacktest.ui.ConfirmationScreen
import com.example.jetpacktest.ui.DateDialogInput
import com.example.jetpacktest.util.Response
import kotlinx.coroutines.launch

@Composable
fun UserForm(user: User = User(), onChange: (User) -> Unit = { }) {
    var firstName by remember { mutableStateOf(user.first_name) }
    var lastName by remember { mutableStateOf(user.last_name) }
    var birthDate by remember { mutableStateOf(user.birth_date) }

    // could have a parent Form component for unifying form UI
    Column {
        CharacterTextInput(firstName, { firstName = it }, true) { Text(stringResource(R.string.firstName)) }
        CharacterTextInput(lastName, { lastName = it }) { Text(stringResource(R.string.lastName)) }
        DateDialogInput(birthDate, { birthDate = it }) { Text(stringResource(R.string.birthDate)) }
    }

    val newUser = user.copy(
        first_name = firstName,
        last_name = lastName,
        birth_date = birthDate
    )

    onChange(newUser)
}

@Composable
fun UserForm(
    label: @Composable () -> Unit,
    response: Response<User> = Response.Idle,
    onDismiss: () -> Unit = { },
    onConfirm: suspend (User) -> Unit =  { }
) {
    val coroutineScope = rememberCoroutineScope()
    var newUser by remember { mutableStateOf(response.resultOrNull ?: User()) }

    // this should expose loading for the action textbutton so that it doesnt cover the whole screen
    ConfirmationScreen(
        label, response, onDismiss,
        { coroutineScope.launch { onConfirm(newUser) } },
        newUser.first_name?.isNotBlank() == true
    ) {
        UserForm(response.resultOrNull ?: User()) { newUser = it }
    }
}