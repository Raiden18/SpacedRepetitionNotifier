package org.danceofvalkyries.notion.domain.models

data class FlashCardsTablesGroup(
    val group: List<FlashCardTable>
) {
    val totalFlashCardsNeedRevising: Int
        get() = group.sumOf { it.flashCardsNeedRevising }

    val allFlashCards: List<FlashCard>
        get() = group.flatMap { it.flashCards }
}