package com.polo.scanner.verify.view

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polo.scanner.R
import com.polo.scanner.R.string
import com.polo.scanner.scanner.QrScannerUi
import com.polo.scanner.verify.viewmodel.VerifyPalletScannerViewModel
import com.polo.scanner.verify.viewmodel.VerifyPalletScannerViewModel.UiState
import com.polo.ui.PalletCardUi
import com.polo.ui.YnTopAppBar
import com.polo.ui.model.ScanningPallet
import com.polo.ui.model.UiPallet
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPalletScannerRoute(
    pallet: ScanningPallet,
    onBack: () -> Unit,
    viewModel: VerifyPalletScannerViewModel = koinViewModel()
) {
    val uiPallet = remember(pallet) { pallet.toUiPallet() }

    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.barcode_scanner_beep_sound)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    val state by viewModel.state.collectAsState()

    EventEffect(
        event = state.isPalletStatusUpdated,
        onConsumed = {}
    ) {
        mediaPlayer?.start()
        val waitMs = mediaPlayer?.duration?.toLong() ?: 0L
        if (waitMs > 0L) {
            delay(waitMs)
        }
        onBack()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        VerifyPalletScannerScreen(
            state = state,
            pallet = uiPallet,
            onBack = onBack,
            onQrCodeScanned = { code ->
                viewModel.onQrCodeScanned(code.trim(), uiPallet)
            },
            onCodeWrongConsumed = viewModel::scannedIsCodeWrongConsumed,
            onErrorConsumed = viewModel::scannedIsErrorConsumed
        )
    }
}

private fun ScanningPallet.toUiPallet(): UiPallet {
    return UiPallet(
        uid = uid,
        date = date,
        productName = productName,
        productAmount = productAmount,
        createdBy = createdBy,
        warehouseName = warehouseName,
        status = status
    )
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun VerifyPalletScannerScreen(
    state: UiState = UiState(),
    pallet: UiPallet = UiPallet(),
    onBack: () -> Unit = {},
    onQrCodeScanned: (String) -> Unit = {},
    onCodeWrongConsumed: () -> Unit = {},
    onErrorConsumed: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scanErrorMessage = stringResource(id = string.scan_error_message)
    val genericErrorMessage = stringResource(id = string.scan_error_generic_message)

    EventEffect(
        event = state.scannedIsCodeWrong,
        onConsumed = onCodeWrongConsumed
    ) {
        snackbarHostState.showSnackbar(message = scanErrorMessage)
    }

    EventEffect(
        event = state.isError,
        onConsumed = onErrorConsumed
    ) { content ->
        snackbarHostState.showSnackbar(message = content ?: genericErrorMessage)
    }

    Scaffold(
        topBar = {
            YnTopAppBar(
                title = stringResource(id = string.scan_verify_pallet),
                onBack = onBack
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = stringResource(id = string.scan_verify_pallet),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                PalletCardUi(pallet = pallet)
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
        ) {
            QrScannerUi(
                onQrCodeScanned = { code ->
                    onQrCodeScanned(code)
                },
                hintText = stringResource(id = string.scan_verify_hint)
            )
        }
    }
}
