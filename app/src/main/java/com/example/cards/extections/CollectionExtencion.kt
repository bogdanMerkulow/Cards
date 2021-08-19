package com.example.cards.extections

import java.util.*

fun MutableList<*>.swap(from: Int, to: Int) {
    Collections.swap(this, from, to)
}