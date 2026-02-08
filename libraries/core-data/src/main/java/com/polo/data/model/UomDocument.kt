package com.polo.data.model

import com.google.firebase.firestore.DocumentId

data class UomDocument(
    @DocumentId
    val uid: String = "",
    val code: String = "",
    val displayName: String = "",
    val kind: String = "",
    val decimals: Int = 0,
    val active: Boolean = true
)
