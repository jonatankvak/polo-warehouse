package com.polo.domain.model

data class CreatePallet(
    val productUid: String,
    val productAmount: Int,
    val createdBy: String,
    val warehouseUid: String,
    val status: PalletStatus = PalletStatus.CREATED
)
