package com.polo.core_ui

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: IDropdownSelectableText> TextFieldDropDownUi(
    modifier: Modifier = Modifier,
    valueSelected: T? = null,
    label: String,
    readOnly: Boolean = false,
    items: List<T>,
    onValueSelected: (T?) -> Unit = {},
    onQueryChanged: (String) -> Unit = {}
) {

    var textValue by remember { mutableStateOf(TextFieldValue(valueSelected?.displayName ?: "")) }
    val localFocusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var expanded by remember { mutableStateOf(false) }

    if (valueSelected == null) {

//        textValue = TextFieldValue("")
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        TextField(
            readOnly = readOnly,
            modifier = Modifier
                .menuAnchor()
                .focusRequester(focusRequester),
            value = textValue,
            onValueChange = {
                textValue = it
                if (textValue.text != valueSelected?.displayName) onValueSelected(null)
                expanded = true
                onQueryChanged(it.text)
            },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        if (items.isNotEmpty()) {
            ExposedDropdownMenu(
                modifier = Modifier.exposedDropdownSize(true),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            textValue = TextFieldValue(item.displayName, selection = TextRange(item.displayName.length))
                            expanded = false
                            localFocusManager.clearFocus(force = true)
                            onValueSelected(item)
                        },
                        text = {
                            Text(text = item.displayName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.typography.bodyMedium.color)
                        }
                    )
                }
            }
        }
    }
}

interface IDropdownSelectableText {

    val displayName: String
}