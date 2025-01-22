package com.polo.pallet.create.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polo.core_ui.TextFieldDropDownUi
import com.polo.core_ui.model.UiPallet
import com.polo.core_ui.model.UiProduct
import com.polo.core_ui.model.UiWarehouse
import com.polo.pallet.R
import com.polo.pallet.create.viewmodel.CreatePalletViewModel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun CreatePalletBottomSheet(
    state: UiState = UiState(),
    onNewProductQuery: (String) -> Unit = {},
    onCreateClick: (UiProduct, UiWarehouse, Int) -> Unit = { _, _, _, -> },
    onVerifyByScan: (UiPallet) -> Unit = {},
    onDelete: (UiPallet) -> Unit = {}
) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        skipHalfExpanded = true
    )

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        sheetContent = {
           CreatePalletUi(
               state.products,
               state.warehouses,
               onNewQuery = { query ->
                   coroutineScope.launch {
                       delay(500)
                       onNewProductQuery(query)
                   }
               },
               onCreateClick = { p, w, a ->
                   coroutineScope.launch { sheetState.hide() }
                   onCreateClick(p, w, a)
               }
           )
        },
        modifier = Modifier.fillMaxSize()
    ) {

        CreatedPalletsUi(
            state = state,
            onAddButtonClick = { coroutineScope.launch { sheetState.show() } },
            onVerifyByScan = onVerifyByScan,
            onDelete = onDelete
        )
    }
}

@Composable
fun CreatePalletUi(
    products: List<UiProduct> = emptyList(),
    warehouses: List<UiWarehouse> = emptyList(),
    onNewQuery: (String) -> Unit = {},
    onCreateClick: (UiProduct, UiWarehouse, Int) -> Unit = { _, _, _ -> },
) {

    val selectedProduct = remember<MutableState<UiProduct?>> { mutableStateOf(null) }
    val selectedWarehouse = remember<MutableState<UiWarehouse?>> { mutableStateOf(null) }
    val amountText = remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {

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

@OptIn(ExperimentalMaterial3Api::class)
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
        Column {

            Text(
                text = stringResource(id = R.string.create_pallet_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.padding(16.dp))

            TextFieldDropDownUi(
                valueSelected = selectedProduct.value,
                readOnly = true,
                label = stringResource(id = R.string.create_pallet_product),
                items = products,
                onValueSelected = { selectedProduct.value = it },
                onQueryChanged = onNewQuery
            )

            Spacer(modifier = Modifier.padding(16.dp))

            TextField(
                value = amountText.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(text = stringResource(id = R.string.create_pallet_amount)) },
                onValueChange = { amountText.value = it }
            )

            Spacer(modifier = Modifier.padding(16.dp))

            TextFieldDropDownUi(
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
        Modifier.padding(top = 36.dp)
    ) {
        Button(
            enabled = selectedProduct.value != null && selectedWarehouse.value != null && amountText.value.text.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                selectedWarehouse.value?.let { warehouse ->
                    selectedProduct.value?.let { product ->
                        onCreateClick(product, warehouse, amountText.value.text.toInt())
                    }
                }
            }
        ) {
            Text(text = stringResource(id = R.string.create_pallet_btn))
        }
    }
}
