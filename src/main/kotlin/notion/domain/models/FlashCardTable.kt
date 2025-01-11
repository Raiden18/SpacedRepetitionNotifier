package org.danceofvalkyries.notion.domain.models

data class FlashCardTable(
    val id: String,
    val name: String,
    val flashCards: List<FlashCard>
) {
    val flashCardsNeedRevising: Int
        get() = flashCards.count()
}
