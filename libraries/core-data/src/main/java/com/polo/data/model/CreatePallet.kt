package com.polo.data.model

import com.google.firebase.firestore.ServerTimestamp
import com.polo.data.model.CreatePallet.PalletStatus.CREATED
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

data class CreatePallet(
    @ServerTimestamp
    val date: Date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
    val productUid: String,
    val productAmount: Int,
    val createdBy: String,
    val warehouseUid: String,
    val status: PalletStatus = CREATED
) {

    enum class PalletStatus {
        CREATED,
        READY,
        TRANSPORT
    }
}
