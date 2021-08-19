package com.example.cards.models

data class GameCard(
    override val rare: Int,
    override val lvl: Int,
    override val image: Int): Card(rare, lvl, image)