package com.polo.ui.model

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
    val status: PalletStatus = CREATED
)
