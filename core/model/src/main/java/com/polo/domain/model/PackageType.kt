package com.polo.domain.model

data class PackageType(
    val uid: String,
    val label: String,
    val tareWeightKg: Float,
    val defaultQuantity: Int,
    val defaultUomUid: String,
    val active: Boolean
)
