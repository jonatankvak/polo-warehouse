package com.polo.domain.model

data class Pallet(
    val uid: String,
    val dateEpochMillis: Long,
    val productUid: String,
    val productAmount: Int,
    val createdBy: String,
    val warehouseUid: String,
    val status: PalletStatus
)
