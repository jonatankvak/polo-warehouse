package com.polo.authentication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.polo.authentication.R
import com.polo.authentication.viewmodel.VerificationViewModel
import com.polo.authentication.viewmodel.VerificationViewModel.UiState
import com.polo.core.theme.AppTheme
import com.polo.core_ui.OtpTextField
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationFragment: Fragment() {

    private val viewModel: VerificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    Surface {

                        val state by viewModel.state.collectAsState()

                        VerifyScreen(state) {
                            findNavController().navigate(VerificationFragmentDirections.navigateToDashboard())
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun VerifyScreen(
        state: UiState = UiState(isUserSignedIn = true),
        onSignIn: () -> Unit = {}
    ) {

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (state.isLoading) Arrangement.Top else Arrangement.SpaceBetween
            ) {
                HeaderUi()
                when {
                    state.isNameProvided -> onSignIn()
                    state.isUserSignedIn -> EnterNameUi(state)
                    state.isLoading -> {
                        Spacer(modifier = Modifier.padding(vertical = 120.dp))
                        CircularProgressIndicator()
                    }
                    state.isCodeSent -> EnterCodeUi(state)
                    else -> EnterPhoneNumberUi(state)
                }
            }
        }
    }

    @Preview
    @Composable
    fun HeaderUi() {
        Box(
            modifier = Modifier.padding(top = 24.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {
                Image(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFFFC72C),
                            shape = CircleShape
                        )
                        .size(160.dp)
                        .padding(8.dp),
                    imageVector = ImageVector.vectorResource(id = com.polo.core.R.drawable.ic_brand),
                    contentDescription = null
                )
                Text(
                    text = stringResource(id = com.polo.core.R.string.app_name_title),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }

    @Preview
    @Composable
    fun EnterCodeUi(
        state: UiState = UiState()
    ) {

        var otpCode by remember {
            mutableStateOf("")
        }

        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.enter_token),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.padding(16.dp))
                OtpTextField(
                    modifier = Modifier.padding(24.dp),
                    otpText = otpCode,
                    onOtpTextChange = { text, isFull ->
                        otpCode = text
                        if (isFull) viewModel.sendOtpToken(otpCode)
                    },
                    errorMessage = state.errorMessage
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Button(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        onClick = { viewModel.resendToken(requireActivity()) }
                    ) {
                        Text(text = stringResource(id = R.string.resend_token_btn))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(36.dp))

        Box(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 24.dp),
                enabled = otpCode.length >= 6,
                onClick = { viewModel.sendOtpToken(otpCode) },
                shape = Shapes().extraLarge
            ) {
                Text(
                    text = stringResource(id = R.string.verify_btn),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun EnterPhoneNumberUi(
        state: UiState = UiState()
    ) {

        val countryPrefix = stringResource(id = R.string.country_prefix)

        var text by remember {
            mutableStateOf(
                TextFieldValue(
                    text = countryPrefix,
                    selection = TextRange(4)
                )
            )
        }

        val focusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier.padding(top = 24.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.enter_phone_number),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.padding(16.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            focusRequester.requestFocus()
                        },
                    value = text,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                    onValueChange = { newText ->
                        text = newText
                    },
                    shape = Shapes().extraLarge,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    isError = state.isError,
                    supportingText = {
                        if (state.isError) {
                            Text(text = state.errorMessage)
                        }
                    }
                )

                Spacer(modifier = Modifier.padding(16.dp))
            }
        }

        Box(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 24.dp),
                enabled = text.text.length >= 9,
                onClick = { viewModel.verifyPhoneNumber(requireActivity(), text.text) },
                shape = Shapes().extraLarge
            ) {
                Text(
                    text = stringResource(id = R.string.verify_btn),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun EnterNameUi(
        state: UiState = UiState()
    ) {

        var firstName by remember { mutableStateOf(TextFieldValue()) }
        var lastName by remember { mutableStateOf(TextFieldValue()) }

        val firstNameRequester = remember { FocusRequester() }
        val lastNameRequester = remember {
            FocusRequester()
        }

        Box(
            modifier = Modifier
                .padding(top = 24.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.enter_name),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .focusRequester(firstNameRequester),
                    value = firstName,
                    keyboardActions = KeyboardActions(
                        onNext = { lastNameRequester.requestFocus() }
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    onValueChange = { newText -> firstName = newText },
                    shape = Shapes().extraLarge,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    placeholder = { Text(text = stringResource(id = R.string.first_name)) }
                )

                Spacer(modifier = Modifier.padding(16.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .focusRequester(lastNameRequester),
                    value = lastName,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    onValueChange = { newText ->
                        lastName = newText
                    },
                    shape = Shapes().extraLarge,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    placeholder = {
                        Text(text = stringResource(id = R.string.last_name))
                    },
                    isError = state.isError,
                    supportingText = { Text(text = state.errorMessage)}
                )
            }
        }

        Box(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 24.dp),
                enabled = firstName.text.isNotEmpty() && lastName.text.isNotEmpty(),
                onClick = { viewModel.saveName(firstName.text, lastName.text) },
                shape = Shapes().extraLarge
            ) {
                Text(
                    text = stringResource(id = R.string.confirm_btn),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}