package com.polo.data.model

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.polo.data.model.CreatePallet.PalletStatus
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ScanningPallet(
    val uid: String = "",
    val date: String = "",
    val productName: String = "",
    val productAmount: Int = -1,
    val createdBy: String = "",
    val warehouseName: String = "",
    val status: PalletStatus = CREATED,
): Parcelable
