package com.polo.data.model

import com.google.firebase.firestore.DocumentId

data class PackageTypeDocument(
    @DocumentId
    val uid: String = "",
    val label: String = "",
    val tareWeightKg: Float = 0f,
    val defaultQuantity: Int = 0,
    val defaultUomUid: String = "",
    val active: Boolean = true
)
