package com.polo.pallet.read.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.polo.core_ui.PalletCardBodyUi
import com.polo.core_ui.PalletCardTitleUi
import com.polo.core_ui.SlideToUnlock
import com.polo.data.model.CreatePallet.PalletStatus
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import com.polo.data.model.CreatePallet.PalletStatus.READY
import com.polo.data.model.CreatePallet.PalletStatus.TRANSPORT
import com.polo.pallet.read.viewmodel.ReadPalletViewModel
import com.polo.pallet.read.viewmodel.ReadPalletViewModel.UiState
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.launch

@Composable
fun ReadPalletRoute(
    palletUid: String,
    onBack: () -> Unit,
    viewModel: ReadPalletViewModel = hiltViewModel()
) {
    LaunchedEffect(palletUid) {
        viewModel.getPallet(palletUid)
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isDissolved) {
        if (state.isDissolved) {
            onBack()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        ReadPalletScreen(
            state = state,
            onSwipe = viewModel::changeStatus,
            onErrorConsumed = viewModel::errorConsumed,
            onBack = onBack
        )
    }
}

@Composable
fun ReadPalletScreen(
    state: UiState = UiState(),
    onSwipe: () -> Unit = {},
    onErrorConsumed: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    EventEffect(event = state.isError, onConsumed = onErrorConsumed) {
        scope.launch {
            snackbarHostState.showSnackbar(message = "Looks like we can't find that pallet!")
            onBack()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            state.pallet?.let {

                val text = when {
                    it.status == CREATED -> "Swipe to verify"
                    it.status == TRANSPORT -> "Swipe to unload"
                    state.isFinalDestination -> "Swipe to dissolve"
                    else -> "Swipe to transport"
                }

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    PalletCardTitleUi(it)
                    PalletCardBodyUi(it)
                    PalletStatusUi(it.status)
                }

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    SlideToUnlock(
                        text = text,
                        isLoading = state.isLoading,
                        onUnlockRequested = onSwipe
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PalletStatusUi(
    palletStatus: PalletStatus = READY
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusable(false)
            .clickable(false, onClick = {}),
        shape = TextFieldDefaults.outlinedShape,
        readOnly = true,
        value = palletStatus.name,
        onValueChange = { },
        label = {
            Text(
                text = stringResource(id = com.polo.ui.R.string.pallet_status),
                overflow = TextOverflow.Visible
            )
        }
    )
}
