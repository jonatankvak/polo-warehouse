package com.polo.scanner.verify.view

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration.Long
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.polo.core.theme.AppTheme
import com.polo.core_ui.PalletCardUi
import com.polo.core_ui.model.UiPallet
import com.polo.scanner.R
import com.polo.scanner.R.string
import com.polo.scanner.scanner.QrScannerUi
import com.polo.scanner.verify.viewmodel.VerifyPalletScannerViewModel
import com.polo.scanner.verify.viewmodel.VerifyPalletScannerViewModel.UiState
import dagger.hilt.android.AndroidEntryPoint
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.delay

@AndroidEntryPoint
class VerifyPalletScannerFragment: Fragment() {

    private val args by navArgs<VerifyPalletScannerFragmentArgs>()
    private val viewModel by viewModels<VerifyPalletScannerViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val pallet = UiPallet(
            uid = args.pallet.uid,
            date = args.pallet.date,
            productName = args.pallet.productName,
            productAmount = args.pallet.productAmount,
            createdBy = args.pallet.createdBy,
            warehouseName = args.pallet.warehouseName,
            status = args.pallet.status
        )

        val mediaPlayer = MediaPlayer.create(context, R.raw.barcode_scanner_beep_sound)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        val state by viewModel.state.collectAsState()

                        EventEffect(
                            event = state.isPalletStatusUpdated,
                            onConsumed = {}
                        ) {
                            mediaPlayer.start()
                            delay(mediaPlayer.duration.toLong())
                            findNavController().popBackStack()
                        }

                        VerifyPalletScannerScreen(state, pallet)
                    }
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    @Preview
    @Composable
    fun VerifyPalletScannerScreen(
        state: UiState = UiState(),
        pallet: UiPallet = UiPallet()
    ) {

        val scaffoldState: ScaffoldState = rememberScaffoldState()
        val scanErrorMessage = stringResource(id = string.scan_error_message)
        val genericErrorMessage = stringResource(id = string.scan_error_generic_message)
        
        
        EventEffect(
            event = state.scannedIsCodeWrong,
            onConsumed = viewModel::scannedIsCodeWrongConsumed
        ) {
            
            scaffoldState.snackbarHostState.showSnackbar(
                message = scanErrorMessage,
                duration = Long
            )
        }

        EventEffect(
            event = state.isError,
            onConsumed = viewModel::scannedIsErrorConsumed
        ) { content ->
            scaffoldState.snackbarHostState.showSnackbar(
                message = content ?: genericErrorMessage ,
                duration = Long
            )
        }

        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom
                ) {

                    Text(
                        modifier = Modifier
                            .padding(bottom = 16.dp),
                        text = stringResource(id = string.scan_verify_pallet),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    PalletCardUi(
                        pallet = pallet
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier.padding(it)
            ) {
                QrScannerUi { code ->
                    viewModel.onQrCodeScanned(code.trim(), pallet)
                }
            }
        }
    }
}