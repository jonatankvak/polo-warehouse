package com.polo.domain.model

data class Product(
    val uid: String,
    val idNumber: Int,
    val name: String,
    val barCode: Long,
    val price: Float,
    val transportPackage: String
)
