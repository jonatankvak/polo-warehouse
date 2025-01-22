package com.polo.core_ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polo.core.R.drawable
import com.polo.core_ui.model.UiPallet
import com.polo.ui.R

@ExperimentalMaterial3Api
@Preview
@Composable
fun PalletCardUi(
    modifier: Modifier = Modifier,
    pallet: UiPallet = UiPallet(),
    onScanControl: (() -> Unit)? = null,
    onDeleteControl: (() -> Unit)? = null
) {
    ExpandableCard(
        modifier = modifier,
        expanded = pallet.expanded.value,
        onExtended = { isExtended -> pallet.expanded.value = !isExtended },
        titleContent = { PalletCardTitleUi(pallet) },
        bodyContent = { PalletCardBodyUi(pallet) },
        controlContent = onScanControl?.let {
            { PalletCardControlUi(onVerifyByScan = onScanControl, onDelete = onDeleteControl) }
        } ?: {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PalletCardTitleUi(
    pallet: UiPallet = UiPallet()
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusable(false)
                    .clickable(false, onClick = {}),
                shape = TextFieldDefaults.outlinedShape,
                readOnly = true,
                value = pallet.uid,
                onValueChange = { },
                label = { Text(text = stringResource(id = R.string.pallet_id), overflow = TextOverflow.Visible) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .focusable(false)
                    .clickable(false, onClick = {})
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .focusable(false)
                        .clickable(false, onClick = {}),
                    shape = TextFieldDefaults.outlinedShape,
                    readOnly = true,
                    value = pallet.productName,
                    onValueChange = { },
                    label = { Text(text = stringResource(id = R.string.pallet_name)) }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .focusable(false)
                    .clickable(false, onClick = {})
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .clickable(false, onClick = {}),
                    shape = TextFieldDefaults.outlinedShape,
                    readOnly = true,
                    value = pallet.productAmount.toString(),
                    onValueChange = { },
                    label = { Text(text = stringResource(id = R.string.pallet_amount), overflow = TextOverflow.Visible) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PalletCardBodyUi(
    pallet: UiPallet = UiPallet()
) {
    Column {

        OutlinedTextField(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .clickable(false, onClick = {}),
            shape = TextFieldDefaults.outlinedShape,
            readOnly = true,
            value = pallet.date,
            onValueChange = { },
            label = { Text(text = stringResource(id = R.string.pallet_date)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .clickable(false, onClick = {}),
            shape = TextFieldDefaults.outlinedShape,
            readOnly = true,
            value = pallet.warehouseName,
            onValueChange = { },
            label = { Text(text = stringResource(id = R.string.pallet_warehouse)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable(false, onClick = {}),
            shape = TextFieldDefaults.outlinedShape,
            readOnly = true,
            value = pallet.createdBy,
            onValueChange = { },
            label = { Text(text = stringResource(id = R.string.pallet_res_person)) }
        )
    }
}

@Preview
@Composable
fun PalletCardControlUi(
    onVerifyByScan: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(
            onClick = onVerifyByScan ?: {}
        ) {
            Icon(
                painter = painterResource(id = drawable.ic_qr_code_scanner),
                contentDescription = null
            )
        }

//        Button(
//            onClick = {}
//        ) {
//            Icon(
//                painter = painterResource(id = drawable.ic_print),
//                contentDescription = null
//            )
//        }

        Button(
            onClick = onDelete ?: {}
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }
    }
}