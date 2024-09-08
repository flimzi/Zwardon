package com.example.jetpacktest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// this should honestly inherit from Screen class which has the different route properties etc
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(navController: NavController, authViewModel: AuthViewModel, user: User = User(0, 2)) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var state by remember { mutableStateOf<Response<Int>>(Response.Idle) }
    var readOnly by remember { mutableStateOf(false) }
    val firstName = remember { mutableStateOf(user.first_name ?: "") }
    val lastName = remember { mutableStateOf(user.last_name ?: "") }
    var datePickerVisible by remember { mutableStateOf(false) }
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
        Text(
            if (user.id == 0)
                stringResource(Screen.AddUser.name)
            else
                "Edit user"
        )

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
                        val newUser = user.copy(
                            first_name = firstName.value,
                            last_name = lastName.value,
                            birth_date = datePickerState.selectedDateMillis?.let {
                                Instant.fromEpochMilliseconds(it)
                            }
                            //                            birth_date = datePickerState.selectedDateMillis?.let {
//                                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
//                            }
                        )

                        state = try {
                            val accessToken = authViewModel.accessToken.value.orEmpty()

                            val userId = if (user.id == 0) {
                                Api.Users.add(accessToken, newUser).body<Int>()
                            } else {
                                Api.Users.update(accessToken, user.id, newUser)
                                user.id
                            }

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
                Text(
                    if (user.id == 0)
                        stringResource(Screen.AddUser.name)
                    else
                        "Edit user"
                )
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
                Toast.makeText(context, "User created (${currentState.result})", Toast.LENGTH_LONG).show()
            }
        }
        else -> { }
    }
}