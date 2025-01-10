package org.danceofvalkyries.notion.domain.models

data class FlashCard(
    val memorizedInfo: String,
    val example: String,
    val answer: String,
)