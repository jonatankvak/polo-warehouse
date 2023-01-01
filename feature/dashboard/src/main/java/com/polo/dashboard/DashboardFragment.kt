package com.polo.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.viewModels
import com.polo.core.theme.AppTheme
import com.polo.dashboard.DashboardViewModel.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel : DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val state by viewModel.state.collectAsState()
                    DashboardScreen(state)
                }
            }
        }
    }

    @Composable
    @Preview
    fun DashboardScreen(
        state: UiState = UiState()
    ) {
        Column {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Welcome ${state.name}")
            }
        }
    }
}