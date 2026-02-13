package com.polo.authentication.view

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.polo.authentication.R
import com.polo.authentication.viewmodel.VerificationViewModel
import com.polo.authentication.viewmodel.VerificationViewModel.UiState
import com.polo.ui.OtpTextField
import com.polo.ui.YnTopAppBar

@Composable
fun VerificationRoute(
    onSignedIn: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val activity = LocalContext.current as? Activity

    LaunchedEffect(state.isNameProvided) {
        if (state.isNameProvided) {
            onSignedIn()
        }
    }

    VerifyScreen(
        state = state,
        onVerifyPhoneNumber = { phoneNumber ->
            activity?.let { viewModel.verifyPhoneNumber(it, phoneNumber) }
        },
        onSendOtp = viewModel::sendOtpToken,
        onResendToken = {
            activity?.let { viewModel.resendToken(it) }
        },
        onSaveName = viewModel::saveName,
        onBackFromCode = viewModel::backToPhoneEntry,
        onBackFromName = viewModel::backToCodeEntry
    )
}

@Preview
@Composable
fun VerifyScreen(
    state: UiState = UiState(isUserSignedIn = true),
    onVerifyPhoneNumber: (String) -> Unit = {},
    onSendOtp: (String) -> Unit = {},
    onResendToken: () -> Unit = {},
    onSaveName: (String, String) -> Unit = { _, _ -> },
    onBackFromCode: () -> Unit = {},
    onBackFromName: () -> Unit = {}
) {
    val title = when {
        state.isUserSignedIn -> stringResource(id = R.string.complete_profile_title)
        state.isCodeSent -> stringResource(id = R.string.verify_code_title)
        else -> stringResource(id = R.string.welcome_title)
    }
    val onBack = when {
        state.isUserSignedIn -> onBackFromName
        state.isCodeSent -> onBackFromCode
        else -> null
    }

    Scaffold(
        topBar = {
            YnTopAppBar(title = title, onBack = onBack)
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.isNameProvided -> Unit
                state.isUserSignedIn -> EnterNameUi(state = state, onSaveName = onSaveName)
                state.isCodeSent -> EnterCodeUi(state = state, onSendOtp = onSendOtp, onResendToken = onResendToken)
                else -> EnterPhoneNumberUi(state = state, onVerifyPhoneNumber = onVerifyPhoneNumber)
            }
        }
    }
}

@Preview
@Composable
fun EnterCodeUi(
    state: UiState = UiState(phoneNumber = "+381999999333"),
    onSendOtp: (String) -> Unit = {},
    onResendToken: () -> Unit = {}
) {
    var otpCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.enter_verification_code_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(id = R.string.verification_sent_to, state.phoneNumber),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OtpTextField(
                modifier = Modifier.padding(top = 20.dp),
                otpText = otpCode,
                onOtpTextChange = { text, _ ->
                    otpCode = text
                },
                errorMessage = state.errorMessage
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onResendToken) {
                    Text(text = stringResource(id = R.string.resend_code_btn))
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = otpCode.length >= 6,
            onClick = { onSendOtp(otpCode) }
        ) {
            Text(text = stringResource(id = R.string.verify_btn))
        }
    }
}

@Preview
@Composable
fun EnterPhoneNumberUi(
    state: UiState = UiState(),
    onVerifyPhoneNumber: (String) -> Unit = {}
) {
    val countryPrefix = stringResource(id = R.string.country_prefix)
    var text by remember { mutableStateOf(TextFieldValue(text = countryPrefix)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.sign_in_phone_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(id = R.string.sign_in_phone_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                value = text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                onValueChange = { newText -> text = newText },
                singleLine = true,
                label = { Text(text = stringResource(id = R.string.phone_number_label)) },
                isError = state.isError,
                supportingText = {
                    if (state.isError && state.errorMessage.isNotBlank()) {
                        Text(text = state.errorMessage)
                    }
                }
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = text.text.length >= 9,
            onClick = { onVerifyPhoneNumber(text.text) }
        ) {
            Text(text = stringResource(id = R.string.verify_btn))
        }
    }
}

@Preview
@Composable
fun EnterNameUi(
    state: UiState = UiState(),
    onSaveName: (String, String) -> Unit = { _, _ -> }
) {
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.whats_your_name_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(id = R.string.whats_your_name_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                modifier = Modifier.padding(top = 22.dp),
                text = stringResource(id = R.string.first_name_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = firstName,
                onValueChange = { firstName = it },
                singleLine = true,
                placeholder = { Text(text = stringResource(id = R.string.first_name)) }
            )

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(id = R.string.last_name_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = lastName,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { lastName = it },
                singleLine = true,
                placeholder = { Text(text = stringResource(id = R.string.last_name)) },
                isError = state.isError,
                supportingText = {
                    if (state.isError && state.errorMessage.isNotBlank()) {
                        Text(text = state.errorMessage)
                    }
                }
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = firstName.text.isNotEmpty() && lastName.text.isNotEmpty(),
            onClick = { onSaveName(firstName.text, lastName.text) }
        ) {
            Text(text = stringResource(id = R.string.confirm_btn))
        }
    }
}
