package com.polo.pallet.create.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polo.core_ui.PalletCardUi
import com.polo.core_ui.model.UiPallet
import com.polo.pallet.R
import com.polo.pallet.create.viewmodel.CreatePalletViewModel.UiState
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch

@Preview
@Composable
fun CreatedPalletsUi(
    state: UiState = UiState(),
    onAddButtonClick: () -> Unit = {},
    onVerifyByScan: (UiPallet) -> Unit = {},
    onDelete: (UiPallet) -> Unit = {}
) {

    Column(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)
    ) {

        val coroutineScope = rememberCoroutineScope()
        val globalExpanded = remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(id = R.string.created_pallets_title),
                style = MaterialTheme.typography.headlineLarge
            )

            Button(
                modifier = Modifier.padding(bottom = 16.dp),
                onClick = {
                    globalExpanded.value = !globalExpanded.value
                    coroutineScope.launch {
                        state.pallets.map { it.expanded.value = globalExpanded.value }
                    }
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = if (globalExpanded.value) {
                            R.drawable.expand_all_24dp
                        } else {
                            R.drawable.collapse_all_24dp
                        }
                    ) ,
                    contentDescription = null
                )
            }
        }

        Box {
            PalletListUi(
                state = state,
                onVerifyByScan = onVerifyByScan,
                onDelete = onDelete
            )
            AddButtonUi(onAddButtonClick = onAddButtonClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PalletListUi(
    state: UiState = UiState(),
    onVerifyByScan: (UiPallet) -> Unit = {},
    onDelete: (UiPallet) -> Unit = {}
) {

    Box {
        if (state.isLoading) {
            LoadingPalletsUi()
        } else {
            LazyColumn {
                items(items = state.pallets, key = { it.uid }, contentType = { UiPallet::class }) { item ->
                    PalletCardUi(
                        pallet = item,
                        onScanControl = { onVerifyByScan.invoke(item) },
                        onDeleteControl = { onDelete(item) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingPalletsUi() {
    Column {
        (0..5).forEach { _ ->
            PalletCardUi(
                modifier = Modifier.shimmer(),
                pallet = UiPallet(uid = "unique", productName = "Pallet", productAmount = 9999)
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
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onAddButtonClick,
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add pallet",
            )
        }
    }
}
