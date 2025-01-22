package com.polo.data.model

import com.google.firebase.firestore.DocumentId

data class ProductDocument(
    @DocumentId
    val uid: String = "",
    val idNumber: Int = -1,
    val name: String = "",
    val barCode: Long = -1,
    val price: Float = -1f,
    val transportPackage: String = ""
)
