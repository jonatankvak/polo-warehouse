package com.polo.ui.model

import com.polo.ui.DropdownSelectable

data class UiWarehouse(
    val uid: String = "",
    val name: String = "",
    override val displayName: String = name
): DropdownSelectable
