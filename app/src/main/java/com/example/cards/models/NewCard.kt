package com.example.cards.models

data class NewCard(
    override val rare: Int,
    override val lvl: Int,
    override val image: Int,
    val position: Int) : Card(rare, lvl, image) {
}