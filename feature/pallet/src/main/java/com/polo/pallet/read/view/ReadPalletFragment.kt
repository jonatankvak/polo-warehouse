package com.polo.pallet.read.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.polo.core.theme.AppTheme
import com.polo.core_ui.PalletCardBodyUi
import com.polo.core_ui.PalletCardTitleUi
import com.polo.core_ui.SlideToUnlock
import com.polo.data.model.CreatePallet.PalletStatus
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import com.polo.data.model.CreatePallet.PalletStatus.READY
import com.polo.data.model.CreatePallet.PalletStatus.TRANSPORT
import com.polo.pallet.read.viewmodel.ReadPalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReadPalletFragment: Fragment() {

    private val args by navArgs<ReadPalletFragmentArgs>()
    private val viewModel by viewModels<ReadPalletViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        viewModel.getPallet(args.palletUid)

                        PalletUi(
                            onSwipe = viewModel::changeStatus
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun PalletUi(
        onSwipe: () -> Unit = {}
    ) {

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val state by viewModel.state.collectAsState()

        if (state.isDissolved) {
            findNavController().popBackStack()
        }

        EventEffect(event = state.isError, onConsumed = viewModel::errorConsumed) {
            scope.launch {
                snackbarHostState.showSnackbar(message = "Looks like we can't find that pallet!")
                findNavController().popBackStack()
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                state.pallet?.let {

                    val text = when {
                        it.status == CREATED -> "Swipe to verify"
                        it.status == TRANSPORT -> "Swipe to unload"
                        state.isFinalDestination -> "Swipe to dissolve"
                        else -> "Swipe to transport"
                    }

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        PalletCardTitleUi(it)
                        PalletCardBodyUi(it)
                        PalletStatusUi(it.status)
                    }

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        SlideToUnlock(
                            text = text,
                            isLoading = state.isLoading,
                            onUnlockRequested = onSwipe
                        )
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun PalletStatusUi(
        palletStatus: PalletStatus = READY
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusable(false)
                .clickable(false, onClick = {}),
            shape = TextFieldDefaults.outlinedShape,
            readOnly = true,
            value = palletStatus.name,
            onValueChange = { },
            label = {
                Text(
                    text = stringResource(id = com.polo.ui.R.string.pallet_status),
                    overflow = TextOverflow.Visible
                )
            }
        )
    }
}