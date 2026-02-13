package com.polo.dashboard.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import com.polo.dashboard.R
import com.polo.dashboard.viewmodel.DashboardViewModel
import com.polo.dashboard.viewmodel.DashboardViewModel.UiState
import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiBody
import com.polo.dashboard.viewmodel.PalletListUiModel.PalletListUiHeader
import com.polo.ui.ExpandableCard
import com.polo.ui.YnTopAppBar

@Composable
fun DashboardRoute(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getAllReadyPallets()
    }

    val state by viewModel.state.collectAsState()

    Surface {
        DashboardScreen(
            state = state
        )
    }
}

@Composable
@Preview
fun DashboardScreen(
    state: UiState = UiState()
) {
    Scaffold(
        topBar = {
            YnTopAppBar(
                title = stringResource(id = R.string.dashboard_screen_title)
            )
        }
    ) { paddingValues ->
        DashboardContent(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            name = state.name,
            state = state
        )
    }
}

@Composable
@Preview
fun DashboardContent(
    modifier: Modifier = Modifier,
    name: String = "",
    state: UiState = UiState()
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.dashboard_screen_subtitle, name),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.dashboard_screen_pallet_section),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = stringResource(id = R.string.dashboard_screen_pallet_section_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.palletsUiModels.isEmpty()) {
            DashboardEmptyState()
        } else {
            DashboardPalletListUi(state)
        }
    }
}

@Composable
private fun DashboardEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
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
            text = stringResource(id = R.string.dashboard_screen_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(id = R.string.dashboard_screen_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
@Preview
fun DashboardPalletListUi(
    state: UiState = UiState()
) {
    LazyColumn {
        items(
            state.palletsUiModels.keys.toList(),
            key = { it.warehouse },
            contentType = { PalletListUiHeader::class }
        ) { item ->
            val groups = state.palletsUiModels[item].orEmpty()
            ExpandableCard(
                expanded = item.expanded.value,
                onExtended = { isExpanded -> item.expanded.value = !isExpanded },
                titleContent = {
                    Column {
                        Text(
                            text = item.warehouse,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            modifier = Modifier.padding(top = 2.dp),
                            text = stringResource(
                                id = R.string.dashboard_screen_products_count,
                                groups.size
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                bodyContent = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        groups.forEach { DashboardPalletItemUi(it) }
                    }
                }
            )
        }
    }
}

@Composable
@Preview
fun DashboardPalletItemUi(
    body: PalletListUiBody = PalletListUiBody(
        name = "Kisko 1/3",
        amount = 100,
        count = 3
    )
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = body.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = stringResource(id = R.string.dashboard_screen_pallet_amount_line, body.amount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = stringResource(id = R.string.dashboard_screen_pallet_count_line, body.count),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
