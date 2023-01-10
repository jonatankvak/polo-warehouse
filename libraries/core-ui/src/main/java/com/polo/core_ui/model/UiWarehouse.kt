package com.polo.core_ui.model

import com.polo.core_ui.IDropdownSelectableText

data class UiWarehouse(
    val uid: String = "",
    val name: String = "",
    override val displayName: String = name
): IDropdownSelectableText
