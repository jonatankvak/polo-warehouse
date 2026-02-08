package com.polo.core_ui.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.polo.domain.model.PalletStatus
import com.polo.domain.model.PalletStatus.CREATED

data class UiPallet(
    val uid: String = "",
    val date: String = "",
    val productName: String = "",
    val productAmount: Int = -1,
    val createdBy: String = "",
    val warehouseName: String = "",
    val warehouseUid: String = "",
    val status: PalletStatus = CREATED,
    val expanded: MutableState<Boolean> = mutableStateOf(false)
)
