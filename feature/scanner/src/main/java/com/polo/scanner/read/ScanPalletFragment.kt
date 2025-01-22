package com.polo.scanner.read

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.polo.core.theme.AppTheme
import com.polo.scanner.R
import com.polo.scanner.scanner.QrScannerUi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanPalletFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val mediaPlayer = MediaPlayer.create(context, R.raw.barcode_scanner_beep_sound)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        QrScannerUi { scannedText ->
                            mediaPlayer.start()
                            findNavController().navigate(
                                ScanPalletFragmentDirections.navigateToPallet(scannedText)
                            )
                        }
                    }
                }
            }
        }
    }

}