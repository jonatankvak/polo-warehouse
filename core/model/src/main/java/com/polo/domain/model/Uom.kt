package com.polo.domain.model

data class Uom(
    val uid: String,
    val code: String,
    val displayName: String,
    val kind: UomKind,
    val decimals: Int,
    val active: Boolean
)

enum class UomKind {
    COUNT,
    WEIGHT,
    VOLUME
}
