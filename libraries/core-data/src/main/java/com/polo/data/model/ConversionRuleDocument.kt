package com.polo.data.model

import com.google.firebase.firestore.DocumentId

data class ConversionRuleDocument(
    @DocumentId
    val uid: String = "",
    val productUid: String = "",
    val fromUomUid: String = "",
    val toUomUid: String = "",
    val factor: Double = 1.0,
    val rounding: Int = 0,
    val active: Boolean = true
)
