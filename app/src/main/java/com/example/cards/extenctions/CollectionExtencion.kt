package com.example.cards.extenctions

import java.util.*

fun MutableList<*>.swap(from: Int, to: Int) {
    Collections.swap(this, from, to)
}