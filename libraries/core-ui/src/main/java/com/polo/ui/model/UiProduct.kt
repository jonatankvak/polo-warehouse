package com.polo.ui.model

import com.polo.ui.DropdownSelectable

data class UiProduct(
    val uid: String = "",
    val idNumber: Int = -1,
    val name: String = "",
    val barCode: Long = -1,
    val price: Float? = null,
    val transportPackage: String = "",
    override val displayName: String = name
): DropdownSelectable
