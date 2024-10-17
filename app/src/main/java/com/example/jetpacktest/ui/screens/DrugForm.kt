package com.example.jetpacktest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.jetpacktest.R
import com.example.jetpacktest.data.AmountUnit
import com.example.jetpacktest.data.Drug
import com.example.jetpacktest.ui.DropDownField
import com.example.jetpacktest.ui.DropDownListElement

@Composable
fun DrugForm(
    drug: Drug = Drug(),
    onChange: (Drug) -> Unit = { }
) {
    var drugState by remember { mutableStateOf(drug) }
    LaunchedEffect(drugState) { onChange(drugState) }

    Column {
        DropDownField(
            Drug.Category.entries.toTypedArray().map { DropDownListElement(it.label, it) },
            { drugState = drugState.copy(category = it.id) },
            drug.categoryId
        ) {
            Text(stringResource(R.string.drugCategory))
        }

        DropDownField(
            AmountUnit.entries.toTypedArray().map { DropDownListElement(it.short, it) },
            { drugState = drugState.copy(unit = it.id) },
            drug.unitId
        ) {
            Text(stringResource(R.string.amountUnit))
        }

        OutlinedTextField(
            drugState.name,
            { drugState = drugState.copy(name = it) },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.name)) }
        )

        OutlinedTextField(
            drugState.info.orEmpty(),
            { drugState = drugState.copy(info = it) },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.additionalInformation)) },
            minLines = 5
        )
    }
}