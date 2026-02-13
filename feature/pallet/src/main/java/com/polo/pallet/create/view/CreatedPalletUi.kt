package com.polo.pallet.create.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polo.pallet.R
import com.polo.pallet.create.viewmodel.CreatePalletViewModel.UiState
import com.polo.ui.PalletCardUi
import com.polo.ui.model.UiPallet
import com.valentinilk.shimmer.shimmer

@Preview
@Composable
fun CreatedPalletsUi(
    state: UiState = UiState(),
    onAddButtonClick: () -> Unit = {},
    onVerifyByScan: (UiPallet) -> Unit = {},
    onDelete: (UiPallet) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PalletListUi(
                state = state,
                onVerifyByScan = onVerifyByScan,
                onDelete = onDelete
            )
            AddButtonUi(onAddButtonClick = onAddButtonClick)
        }
    }
}

@Preview
@Composable
fun PalletListUi(
    state: UiState = UiState(),
    onVerifyByScan: (UiPallet) -> Unit = {},
    onDelete: (UiPallet) -> Unit = {}
) {
    when {
        state.isLoading -> LoadingPalletsUi()
        state.pallets.isEmpty() -> EmptyPalletsUi()
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                content = {
                    items(items = state.pallets, key = { it.uid }, contentType = { UiPallet::class }) { item ->
                        PalletCardUi(
                            pallet = item,
                            onScanControl = { onVerifyByScan.invoke(item) },
                            onDeleteControl = { onDelete(item) }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyPalletsUi() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(id = R.string.created_pallets_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(id = R.string.created_pallets_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LoadingPalletsUi() {
    Column {
        (0..4).forEach {
            PalletCardUi(
                modifier = Modifier.shimmer(),
                pallet = UiPallet(
                    uid = "PAL-000$it",
                    productName = "Pallet",
                    productAmount = 999
                )
            )
        }
    }
}

@Preview
@Composable
fun AddButtonUi(
    onAddButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp, end = 8.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onAddButtonClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add pallet",
            )
        }
    }
}
