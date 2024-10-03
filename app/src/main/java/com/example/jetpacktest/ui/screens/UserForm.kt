package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.jetpacktest.R
import com.example.jetpacktest.data.User
import com.example.jetpacktest.ui.CharacterTextInput
import com.example.jetpacktest.ui.DateDialogInput

@Composable
fun UserForm(user: User, onChange: (User) -> Unit = { }) {
    var firstName by remember { mutableStateOf(user.first_name) }
    var lastName by remember { mutableStateOf(user.last_name) }
    var birthDate by remember { mutableStateOf(user.birth_date) }

    LaunchedEffect(user) {
        firstName = user.first_name
        lastName = user.last_name
        birthDate = user.birth_date
    }

    LaunchedEffect(firstName, lastName, birthDate) {
        val newUser = user.copy(
            first_name = firstName,
            last_name = lastName,
            birth_date = birthDate
        )

        onChange(newUser)
    }

    Column {
        CharacterTextInput(firstName, { firstName = it }, true) { Text(stringResource(R.string.firstName)) }
        CharacterTextInput(lastName, { lastName = it }) { Text(stringResource(R.string.lastName)) }
        DateDialogInput(birthDate, { birthDate = it }) { Text(stringResource(R.string.birthDate)) }
    }
}