package com.polo.data.model

import com.google.firebase.firestore.DocumentId

data class WarehouseDocument(
    @DocumentId
    val uid: String = "",
    val name: String = ""
)
