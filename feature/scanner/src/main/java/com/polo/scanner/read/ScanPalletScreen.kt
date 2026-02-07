package com.polo.scanner.read

import android.media.MediaPlayer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.polo.scanner.R
import com.polo.scanner.scanner.QrScannerUi

@Composable
fun ScanPalletRoute(
    onPalletScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.barcode_scanner_beep_sound)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        QrScannerUi { scannedText ->
            mediaPlayer?.start()
            onPalletScanned(scannedText)
        }
    }
}
