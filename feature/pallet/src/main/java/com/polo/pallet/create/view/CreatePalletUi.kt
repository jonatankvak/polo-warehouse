package com.polo.pallet.create.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polo.ui.TextFieldDropDownUi
import com.polo.ui.YnTopAppBar
import com.polo.ui.model.UiPallet
import com.polo.ui.model.UiProduct
import com.polo.ui.model.UiWarehouse
import com.polo.pallet.R
import com.polo.pallet.create.viewmodel.CreatePalletViewModel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreatePalletBottomSheet(
    state: UiState = UiState(),
    onNewProductQuery: (String) -> Unit = {},
    onCreateClick: (UiProduct, UiWarehouse, Int) -> Unit = { _, _, _ -> },
    onVerifyByScan: (UiPallet) -> Unit = {},
    onDelete: (UiPallet) -> Unit = {}
) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            YnTopAppBar(
                title = stringResource(id = R.string.created_pallets_title)
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
        ) {
            CreatedPalletsUi(
                state = state,
                onAddButtonClick = { showSheet = true },
                onVerifyByScan = onVerifyByScan,
                onDelete = onDelete
            )

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    dragHandle = null
                ) {
                    CreatePalletUi(
                        products = state.products,
                        warehouses = state.warehouses,
                        onCloseSheet = {
                            coroutineScope.launch {
                                sheetState.hide()
                                showSheet = false
                            }
                        },
                        onNewQuery = { query ->
                            coroutineScope.launch {
                                delay(500)
                                onNewProductQuery(query)
                            }
                        },
                        onCreateClick = { p, w, a ->
                            coroutineScope.launch {
                                sheetState.hide()
                                showSheet = false
                            }
                            onCreateClick(p, w, a)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePalletUi(
    products: List<UiProduct> = emptyList(),
    warehouses: List<UiWarehouse> = emptyList(),
    onCloseSheet: () -> Unit = {},
    onNewQuery: (String) -> Unit = {},
    onCreateClick: (UiProduct, UiWarehouse, Int) -> Unit = { _, _, _ -> },
) {

    val selectedProduct = remember<MutableState<UiProduct?>> { mutableStateOf(null) }
    val selectedWarehouse = remember<MutableState<UiWarehouse?>> { mutableStateOf(null) }
    val amountText = remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .width(40.dp),
                thickness = 3.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.create_pallet_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onCloseSheet) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        InputPalletUi(
            selectedProduct,
            selectedWarehouse,
            amountText,
            products = products,
            warehouses = warehouses,
            onNewQuery = onNewQuery
        )
        CreatePalletButtonUi(
            selectedProduct,
            selectedWarehouse,
            amountText,
            onCreateClick = onCreateClick
        )
    }
}

@Preview
@Composable
fun InputPalletUi(
    selectedProduct: MutableState<UiProduct?> = mutableStateOf(null),
    selectedWarehouse: MutableState<UiWarehouse?> = mutableStateOf(null),
    amountText: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    products: List<UiProduct> = emptyList(),
    warehouses: List<UiWarehouse> = emptyList(),
    onNewQuery: (String) -> Unit = {}
) {
    Box {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.padding(8.dp))

            TextFieldDropDownUi(
                modifier = Modifier.fillMaxWidth(),
                valueSelected = selectedProduct.value,
                readOnly = true,
                label = stringResource(id = R.string.create_pallet_product),
                items = products,
                onValueSelected = { selectedProduct.value = it },
                onQueryChanged = onNewQuery
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = stringResource(id = R.string.create_pallet_amount),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.padding(6.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = amountText.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                placeholder = { Text(text = stringResource(id = R.string.create_pallet_amount_placeholder)) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    errorBorderColor = MaterialTheme.colorScheme.error
                ),
                onValueChange = {
                    if (it.text.isEmpty() || it.text.all(Char::isDigit)) {
                        amountText.value = it
                    }
                }
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextFieldDropDownUi(
                modifier = Modifier.fillMaxWidth(),
                valueSelected = selectedWarehouse.value,
                readOnly = true,
                label = stringResource(id = R.string.create_pallet_warehouse),
                items = warehouses,
                onValueSelected = { selectedWarehouse.value = it }
            )
        }
    }
}

@Preview
@Composable
fun CreatePalletButtonUi(
    selectedProduct: MutableState<UiProduct?> = mutableStateOf(null),
    selectedWarehouse: MutableState<UiWarehouse?> = mutableStateOf(null),
    amountText: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    onCreateClick: (UiProduct, UiWarehouse, Int) -> Unit = { _, _, _ -> }
) {
    Box(
        Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp)
    ) {
        Button(
            enabled = selectedProduct.value != null && selectedWarehouse.value != null && amountText.value.text.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = {
                selectedWarehouse.value?.let { warehouse ->
                    selectedProduct.value?.let { product ->
                        amountText.value.text.toIntOrNull()?.let { amount ->
                            onCreateClick(product, warehouse, amount)
                        }
                    }
                }
            }
        ) {
            Text(text = stringResource(id = R.string.create_pallet_btn))
        }
    }
}
