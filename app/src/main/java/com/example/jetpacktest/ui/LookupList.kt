package com.example.jetpacktest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.jetpacktest.R

@Composable
fun <T> LookupList(
    elements: List<DropDownListElement<T>> = listOf(),
    onSelected: (T) -> Unit = { },
    label: @Composable () -> Unit = { }
) {
    var searchString by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(searchString, { searchString = it }, Modifier.fillMaxWidth(), label = label)

        if (elements.isEmpty()) Text(stringResource(R.string.nothing))

        elements.filter{ it.label.startsWith(searchString) }.forEach { (label, value) ->
            ListItem(
                { Text(label) },
                Modifier.fillMaxWidth().clickable { onSelected(value) }
            )
        }
    }
}