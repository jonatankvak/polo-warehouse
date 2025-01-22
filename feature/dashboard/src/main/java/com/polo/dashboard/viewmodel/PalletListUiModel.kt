package com.polo.dashboard.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class PalletListUiModel {

    data class PalletListUiHeader(
        val warehouse: String,
        val expanded: MutableState<Boolean> = mutableStateOf(false)
    ): PalletListUiModel()

    data class PalletListUiBody(
        val name: String,
        val amount: Int,
        val count: Int
    ): PalletListUiModel()
}
