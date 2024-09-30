package com.example.jetpacktest.ui.screens

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.jetpacktest.R
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.data.User
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.ui.CharacterTextInput
import com.example.jetpacktest.ui.ConfirmationDialog
import com.example.jetpacktest.ui.DateDialogInput
import com.example.jetpacktest.util.Response
import com.example.jetpacktest.util.onError
import com.example.jetpacktest.util.onResult
import kotlinx.coroutines.launch

@Composable
fun EditUserDialog(
    user: User = User(),
    onDismiss: () -> Unit = { },
    onConfirm: (User) -> Unit = { }
) {
    var firstName by remember { mutableStateOf(user.first_name) }
    var lastName by remember { mutableStateOf(user.last_name) }
    var birthDate by remember { mutableStateOf(user.birth_date) }

    val newUser = user.copy(
        first_name = firstName,
        last_name = lastName,
        birth_date = birthDate
    )

    ConfirmationDialog(
        { Text("abc") },
        onDismiss,
        { onConfirm(newUser) },
        !firstName.isNullOrBlank(),
    ) {
        CharacterTextInput(firstName, { firstName = it }, true) { Text(stringResource(R.string.firstName)) }
        CharacterTextInput(lastName, { lastName = it }) { Text(stringResource(R.string.lastName)) }
        DateDialogInput(birthDate, { birthDate = it }) { Text(stringResource(R.string.birthDate)) }
    }
}

@Composable
fun EditUserDialog(
    currentUser: AuthenticatedUser,
    userId: Int? = currentUser.details.id,
    navController: NavController? = null,
    snackbarHostState: SnackbarHostState? = null,
    onDismiss: () -> Unit = { navController?.popBackStack() },
    onAdded: (User) -> Unit = { navController?.popBackStack() }
) {
    val coroutineScope = rememberCoroutineScope()
    var response by remember { mutableStateOf<Response<*>>(Response.Idle) }
    var user by remember { mutableStateOf(User()) }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotBlank()) {
            snackbarHostState?.showSnackbar(snackbarMessage)
        }
    }

    LaunchedEffect(userId) {
        if (userId != null) {
            Api.Users.get(currentUser.accessToken, userId).apply {
                onResult { user = it }
                onError { snackbarMessage = it }
                collect { response = it }
            }
        }
    }

    if (response is Response.Loading) {
        LoadingScreen()
    }

    EditUserDialog(user, onDismiss) { newUser ->
        coroutineScope.launch {
            val operation = if (userId != null)
                Api.Users.update(currentUser.accessToken, userId, newUser)
            else
                Api.Users.addGet(currentUser.accessToken, newUser)

            operation.apply {
                onError { snackbarMessage = it }
                onResult(onAdded)
                collect { response = it }
            }
        }
    }
}

@Composable
fun EditUserDialog2(

) {

}