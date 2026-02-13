package com.polo.ui.model

import android.os.Parcelable
import com.polo.domain.model.PalletStatus
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ScanningPallet(
    val uid: String = "",
    val date: String = "",
    val productName: String = "",
    val productAmount: Int = -1,
    val createdBy: String = "",
    val warehouseName: String = "",
    val status: PalletStatus = PalletStatus.CREATED,
): Parcelable
