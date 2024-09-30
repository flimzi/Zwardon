package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.data.Drug
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.ui.Choice
import com.example.jetpacktest.ui.ChoiceDialogInput
import com.example.jetpacktest.ui.Screen
import com.example.jetpacktest.util.Response
import kotlinx.coroutines.launch

@Composable
fun AddDrugDialog(
    currentUser: AuthenticatedUser,
    userId: Int,
    drugId: Int? = null,
    onDismiss: () -> Unit = { },
    onAdded: (Drug) -> Unit = { },
) {
    val coroutineScope = rememberCoroutineScope()
    var drug by remember { mutableStateOf(Drug()) }
    var loading by remember { mutableStateOf(false) }
    // afaik this is the most idiomatic it can be because every property needs to have its own mutablestate
    // but im pretty sure this doesnt update on drug change
    var name by remember { mutableStateOf(drug.name) }
    var unit by remember { mutableStateOf(drug.unitId) }
    var category by remember { mutableStateOf(drug.categoryId) }
    var info by remember { mutableStateOf(drug.info.orEmpty()) }

    fun add(snackbarHostState: SnackbarHostState) {
        coroutineScope.launch {
            val newDrug = drug.copy(category = category.id, name = name, unit = unit.id, info = info)

            Api.Drugs.add(currentUser.accessToken, userId, newDrug).collect {
                loading = it is Response.Loading

                if (it is Response.Result<*>)
                    onAdded(newDrug)
                else if (it is Response.Error)
                    snackbarHostState.showSnackbar(it.message)
            }
        }
    }

    Screen {
//        LaunchedEffect(Unit) {
//            if (drugId != null) {
//                Api.Drugs.get(currentUser.accessToken, userId, drugId).collect {
//                    loading = it is Response.Loading
//
//                    if (it is Response.Result<Drug>)
//                        drug = it.result
//                    else if (it is Response.Error)
//                        snackbarHostState.showSnackbar(it.message)
//                }
//            }
//        }

        ChoiceDialogInput(
            Drug.Category.entries.toTypedArray().map { Choice(it.label, it, mutableStateOf(it == category)) },
            { category = it.first() },
            Modifier.fillMaxWidth(),
            minChosen = 1, maxChosen = 1,
            enabled = !loading
        ) {
            Text("Category")
        }

        OutlinedTextField(
            info, { info = it }, Modifier.fillMaxWidth(),
            label = { Text("Additional information") },
            enabled = !loading,
            minLines = 5
        )
    }
}