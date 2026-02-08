package com.polo.designsystem

data class Consumable<T>(private var value: T?) {

    fun isEmpty() = value == null

    fun getValue(): T? {

        val returnValue = value
        value = null

        return returnValue
    }
}
