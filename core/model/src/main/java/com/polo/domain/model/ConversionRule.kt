package com.polo.domain.model

data class ConversionRule(
    val uid: String,
    val productUid: String,
    val fromUomUid: String,
    val toUomUid: String,
    val factor: Double,
    val rounding: Int,
    val active: Boolean
)
