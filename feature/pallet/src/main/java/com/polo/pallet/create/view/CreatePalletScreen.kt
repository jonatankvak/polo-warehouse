package com.polo.pallet.create.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.polo.ui.model.ScanningPallet
import com.polo.ui.model.UiPallet
import com.polo.pallet.create.viewmodel.CreatePalletViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreatePalletRoute(
    onVerifyByScan: (ScanningPallet) -> Unit,
    viewModel: CreatePalletViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getAllProductsAndWarehouses()
    }

    val state by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        CreatePalletBottomSheet(
            state = state,
            onNewProductQuery = { },
            onCreateClick = { product, warehouse, amount ->
                viewModel.createPallet(product, warehouse, amount)
            },
            onVerifyByScan = { pallet ->
                onVerifyByScan(pallet.toScanningPallet())
            },
            onDelete = { pallet ->
                viewModel.deletePallet(pallet)
            }
        )
    }
}

private fun UiPallet.toScanningPallet(): ScanningPallet {
    return ScanningPallet(
        uid = uid,
        date = date,
        productName = productName,
        productAmount = productAmount,
        createdBy = createdBy,
        warehouseName = warehouseName,
        status = status
    )
}
