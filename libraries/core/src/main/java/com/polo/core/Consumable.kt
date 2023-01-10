package com.polo.core

data class Consumable<T>(private var value: T?) {

    fun isEmpty() = value == null

    fun getValue(): T? {

        val returnValue = value
        value = null

        return returnValue
    }
}