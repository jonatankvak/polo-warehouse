package com.polo.pallet.create.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.polo.core.theme.AppTheme
import com.polo.data.model.Pallet
import com.polo.data.model.ScanningPallet
import com.polo.pallet.create.viewmodel.CreatePalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreatePalletFragment: Fragment() {

    private val viewModel by viewModels<CreatePalletViewModel>()

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val anim = AnimationUtils.loadAnimation(activity, nextAnim)

        anim.setAnimationListener(
            object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    if (enter) {
                        viewModel.getAllProductsAndWarehouses()
                    }
                }
            }
        )

        return anim
    }

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
                        val state by viewModel.state.collectAsState()
                        CreatePalletBottomSheet(
                            state,
                            onNewProductQuery = { },
                            onCreateClick = { product, warehouse, amount ->

                                viewModel.createPallet(product, warehouse, amount)
                            },
                            onVerifyByScan = { pallet ->
                                findNavController().navigate(
                                    CreatePalletFragmentDirections.navigateToVerifyScanner(
                                        ScanningPallet(
                                            uid = pallet.uid,
                                            date = pallet.date,
                                            productName = pallet.productName,
                                            productAmount = pallet.productAmount,
                                            createdBy = pallet.createdBy,
                                            warehouseName = pallet.warehouseName,
                                            status = pallet.status
                                        )
                                    )
                                )
                            },
                            onDelete = { pallet ->

                                viewModel.deletePallet(pallet)
                            }
                        )
                    }
                }
            }
        }
    }
}