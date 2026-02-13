package com.polo.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String = "",
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit = { s: String, b: Boolean -> },
    errorMessage: String = ""
) {

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = otpText,
        onValueChange = {
            if (it.length <= otpCount) {
                onOtpTextChange.invoke(it, it.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(otpCount) { index ->
                        CharView(
                            index = index,
                            text = otpText,
                            otpCount = otpCount
                        )
                        if (index < otpCount - 1) {
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 8.dp))

                if (errorMessage.isNotBlank()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String,
    otpCount: Int
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length || index > text.length -> ""
        else -> text[index].toString()
    }
    val shape = when(index) {
        0 -> RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp, topEnd = 4.dp, bottomEnd = 4.dp)
        otpCount - 1 -> RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 10.dp, bottomEnd = 10.dp)
        else -> RoundedCornerShape(4.dp)
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .border(
                2.dp, when {
                    isFocused -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline
                }, shape
            )
            .padding(0.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
