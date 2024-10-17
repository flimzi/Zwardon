package com.example.jetpacktest.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize

data class DropDownListElement<T>(val label: String, val value: T)

@Composable
fun <T> DropDownField(
    elements: List<DropDownListElement<T>> = listOf(),
    onSelected: (T) -> Unit = { },
    select: T? = null,
    enabled: Boolean = true,
    expand: Boolean = false,
    label: @Composable () -> Unit = { }
) {
    var expanded by remember { mutableStateOf(expand) }
    LaunchedEffect(expand) { expanded = expand }

    var selected by remember { mutableStateOf(select) }
    LaunchedEffect(select) { selected = select }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val selectedElement = elements.firstOrNull { it.value == selected }

    Column {
        OutlinedTextField(
            selectedElement?.label.orEmpty(), { },
            readOnly = true,
            enabled = enabled,
            label = label,
            trailingIcon = {
                IconButton({ expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, "drop down")
                }
            },
            modifier = Modifier
                .onGloballyPositioned { textFieldSize = it.size.toSize() }
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded,
            { expanded = false },
            Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            elements.forEach { (label, value) ->
                DropdownMenuItem(
                    { Text(label) },
                    {
                        selected = value
                        onSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun <T> DropDownSelectionField(
    elements: List<DropDownListElement<T>> = listOf(),
    onSelected: (List<T>) -> Unit = { },
    select: List<T> = listOf(),
    expand: Boolean = false,
    label: @Composable () -> Unit = { }
) {
    // not needed for now
}


@Composable
fun <T> DropDownLookupField(
    elements: List<DropDownListElement<T>> = listOf(),
    onSelected: (T) -> Unit = { },
    enabled: Boolean = true,
    expand: Boolean = false,
    label: @Composable () -> Unit = { }
) {
    var expanded by remember { mutableStateOf(expand) }
    LaunchedEffect(expand) { expanded = expand }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var lookup by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            lookup, { expanded = true; lookup = it },
            enabled = enabled,
            label = label,
            trailingIcon = {
                IconButton({ expanded = !expanded }) {
                    Icon(Icons.Default.Search, "search")
                }
            },
            modifier = Modifier
                .onGloballyPositioned { textFieldSize = it.size.toSize() }
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded, { expanded = false },
            Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            elements.filter { it.label.startsWith(lookup) }.forEach { (label, value) ->
                DropdownMenuItem(
                    { Text(label) },
                    {
                        onSelected(value)
                        expanded = false
                        lookup = ""
                    }
                )
            }
        }
    }
}

@Composable
fun <T> SelectionList(
    elements: List<DropDownListElement<T>> = listOf(),
    onRemoved: (T) -> Unit = { }
) {
    Column {
        elements.forEach { (label, value) ->
            ListItem(
                { Text(label) },
                trailingContent = {
                    IconButton({ onRemoved(value) }) {
                        Icon(Icons.Default.Close, "remove")
                    }
                }
            )
        }
    }
}