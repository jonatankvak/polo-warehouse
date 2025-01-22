package com.polo.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.polo.data.model.CreatePallet.PalletStatus
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import kotlinx.parcelize.Parcelize

@Parcelize
data class PalletDocument(
    @DocumentId
    val uid: String = "",
    @ServerTimestamp
    val date: Timestamp = Timestamp.now(),
    val productUid: String = "",
    val productAmount: Int = -1,
    val createdBy: String = "",
    val warehouseUid: String = "",
    val status: PalletStatus = CREATED,
    val createdAt: Timestamp = Timestamp.now()
): Parcelable