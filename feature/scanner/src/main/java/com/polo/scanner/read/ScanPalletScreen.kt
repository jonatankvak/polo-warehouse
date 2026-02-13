package com.polo.scanner.read

import android.media.MediaPlayer
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.polo.scanner.R
import com.polo.scanner.scanner.QrScannerUi
import com.polo.ui.YnTopAppBar

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

    var lastScannedCode by rememberSaveable { mutableStateOf("") }
    var lastScannedAtMs by rememberSaveable { mutableLongStateOf(0L) }

    Scaffold(
        topBar = {
            YnTopAppBar(
                title = stringResource(id = R.string.scan_pallet_title)
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
        ) {
            QrScannerUi(
                onQrCodeScanned = { scannedText ->
                    val normalizedCode = scannedText.trim()
                    val now = SystemClock.elapsedRealtime()
                    val isDuplicateQuickRescan =
                        normalizedCode == lastScannedCode && now - lastScannedAtMs < 3000L

                    if (isDuplicateQuickRescan) return@QrScannerUi

                    lastScannedCode = normalizedCode
                    lastScannedAtMs = now
                    mediaPlayer?.start()
                    onPalletScanned(normalizedCode)
                },
                hintText = stringResource(id = R.string.scan_pallet_hint)
            )

            ScanPalletBottomPanel(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ScanPalletBottomPanel(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(id = R.string.scan_pallet_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(id = R.string.scan_pallet_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
