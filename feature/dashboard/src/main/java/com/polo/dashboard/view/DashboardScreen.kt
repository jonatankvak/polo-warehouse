package com.polo.dashboard.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.polo.core.R.drawable
import com.polo.core_ui.ExpandableContent
import com.polo.dashboard.R
import com.polo.dashboard.viewmodel.DashboardViewModel
import com.polo.dashboard.viewmodel.DashboardViewModel.UiState
import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiBody
import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiHeader

@Composable
fun DashboardRoute(
    onCreatePallet: () -> Unit,
    onEditPallet: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getAllReadyPallets()
    }

    val state by viewModel.state.collectAsState()

    Surface {
        DashboardScreen(
            state = state,
            onCreatePallet = onCreatePallet,
            onEditPallet = onEditPallet
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DashboardScreen(
    state: UiState = UiState(),
    onCreatePallet: () -> Unit = {},
    onEditPallet: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            DashboardBottomNavigation(
                onCreatePallet = onCreatePallet,
                onEditPallet = onEditPallet
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            DashboardContent(state = state)
        }
    }
}

@Composable
@Preview
fun DashboardBottomNavigation(
    onCreatePallet: () -> Unit = {},
    onEditPallet: () -> Unit = {}
) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = onCreatePallet,
            icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.nav_create)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = onEditPallet,
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = drawable.ic_qr_code_scanner),
                    contentDescription = null
                )
            },
            label = { Text(text = stringResource(id = R.string.nav_scan)) }
        )
    }
}

@Composable
@Preview
fun DashboardContent(
    modifier: Modifier = Modifier,
    state: UiState = UiState()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        DashboardTitleUi(state.name)
        DashboardPalletListUi(state)
    }
}

@Composable
@Preview
fun DashboardTitleUi(
    name: String = "Jovan Jocovic"
) {
    Column(
        modifier = Modifier
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.dashboard_screen_title),
            style = MaterialTheme.typography.headlineLarge
        )
        Text(text = stringResource(id = R.string.dashboard_screen_subtitle, name))
    }
}

@Composable
@Preview
fun DashboardPalletListUi(
    state: UiState = UiState()
) {
    Text(
        text = stringResource(id = R.string.dashboard_screen_pallet_section),
        style = MaterialTheme.typography.headlineSmall
    )

    LazyColumn {
        items(
            state.palletsUiModels.keys.toList(),
            key = { it.warehouse },
            contentType = { PalletListUiHeader::class }
        ) { item ->
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clickable { item.expanded.value = !item.expanded.value }
                ) {
                    Column {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.onBackground)
                                .padding(vertical = 16.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                modifier = Modifier
                                    .padding(vertical = 16.dp, horizontal = 4.dp),
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_warehouse),
                                contentDescription = null
                            )

                            Text(
                                modifier = Modifier
                                    .padding(vertical = 16.dp, horizontal = 4.dp),
                                text = item.warehouse
                            )
                        }


                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.onBackground)
                                .padding(vertical = 16.dp)
                        )
                    }
                }

                ExpandableContent(
                    visible = item.expanded.value,
                ) {
                    Column {
                        state.palletsUiModels[item]?.forEach {
                            DashboardPalletItemUi(it)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DashboardPalletItemUi(
    body: PalletListUiBody = PalletListUiBody(
        name = "Kisko 1/3",
        amount = 100,
        count = 3
    )
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .focusable(false)
                .clickable(false, onClick = {}),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable(false)
                        .clickable(false, onClick = {}),
                    shape = OutlinedTextFieldDefaults.shape,
                    readOnly = true,
                    value = body.name,
                    singleLine = true,
                    onValueChange = { },
                    label = {
                        Text(
                            text = stringResource(id = R.string.dashboard_screen_pallet_name),
                            overflow = TextOverflow.Visible
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Box(
                modifier = Modifier
                    .weight(0.6f)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable(false)
                        .clickable(false, onClick = {}),
                    shape = OutlinedTextFieldDefaults.shape,
                    readOnly = true,
                    value = body.amount.toString(),
                    onValueChange = { },
                    label = {
                        Text(
                            text = stringResource(id = R.string.dashboard_screen_pallet_amount),
                            overflow = TextOverflow.Visible
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))
            Box(
                modifier = Modifier.weight(0.5f)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable(false)
                        .clickable(false, onClick = {}),
                    shape = OutlinedTextFieldDefaults.shape,
                    readOnly = true,
                    value = "x${body.count}",
                    onValueChange = { },
                    label = {
                        Text(
                            text = stringResource(id = R.string.dashboard_screen_pallet_count),
                            overflow = TextOverflow.Visible
                        )
                    }
                )
            }

        }
    }
}
