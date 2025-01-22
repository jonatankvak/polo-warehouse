package com.polo.core_ui.model

import com.polo.core_ui.IDropdownSelectableText

data class UiProduct(
    val uid: String = "",
    val idNumber: Int = -1,
    val name: String = "",
    val barCode: Long = -1,
    val price: Float = -1f,
    val transportPackage: String = "",
    override val displayName: String = name
): IDropdownSelectableText