package com.example.jetpacktest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacktest.R
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.User
import com.example.jetpacktest.navigation.Screen
import com.example.jetpacktest.ui.CharacterTextInput
import com.example.jetpacktest.util.Response
import io.ktor.client.call.body
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

// this should honestly inherit from Screen class which has the different route properties etc
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(navController: NavController, authViewModel: AuthViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var state by remember { mutableStateOf<Response<Int>>(Response.Idle) }
    var readOnly by remember { mutableStateOf(false) }
    var firstName = remember { mutableStateOf("") }
    var lastName = remember { mutableStateOf("") }
    var datePickerVisible by remember { mutableStateOf(false) }
    var datePickerFocused by remember { mutableStateOf(false) }
    var datePickerError by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    }.orEmpty()

    LaunchedEffect(state) {
        readOnly = state is Response.Loading
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(Screen.AddUser.name))

        Spacer(Modifier.height(16.dp))

        CharacterTextInput(R.string.firstName, firstName, true, readOnly)
        CharacterTextInput(R.string.lastName, lastName, true, readOnly)
        
        OutlinedTextField(
            selectedDate, { },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.birthDate)) },
            readOnly = true,
            isError = datePickerError,
            trailingIcon = {
                IconButton({ datePickerVisible = !readOnly }) {
                    Icon(Icons.Default.DateRange, "Select date")
                }
            }
        )

        fun hideDatePicker() {
            datePickerVisible = false
//            datePickerError = datePickerState.selectedDateMillis == null
        }

        if (datePickerVisible) {
            DatePickerDialog(
                onDismissRequest = { datePickerVisible = false },
                confirmButton = {
                    TextButton({ datePickerVisible = false }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton({ datePickerVisible = false }) {
                        Text("Cancel")
                    }
                },
            ) { DatePicker(datePickerState, showModeToggle = false) }
        }

        Spacer(Modifier.height(16.dp))
        Button({
            if (state != Response.Loading) {
                if (firstName.value.isBlank() || lastName.value.isBlank())  {
                    state = Response.Error("Correct input errors")
                } else {
                    coroutineScope.launch {
                        state = Response.Loading
                        val user = User(0, 2,
                            first_name = firstName.value,
                            last_name = lastName.value,
                            birth_date = datePickerState.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
                            }
                        )

                        state = try {
                            val userId = Api.Users.add(authViewModel.accessToken.value.orEmpty(), user).body<Int>()
                            Response.Result(userId)
                        } catch (e : Exception) {
                            Response.ServerError
                        }
                    }
                }
            }
        }, Modifier.fillMaxWidth()) {
            if (state is Response.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                Text(stringResource(R.string.addUser))
            }
        }
    }

    when (val currentState = state) {
        is Response.Error -> {
            LaunchedEffect(state) {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            }
        }
        is Response.Result<Int> -> {
            // navigate to user page?
            navController.navigate(Screen.AddUser.route)

            LaunchedEffect(state) {
                Toast.makeText(context, "User created (${currentState.data})", Toast.LENGTH_LONG).show()
            }
        }
        else -> { }
    }
}