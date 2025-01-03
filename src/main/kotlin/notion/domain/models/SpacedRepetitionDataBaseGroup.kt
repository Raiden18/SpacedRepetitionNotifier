package org.danceofvalkyries.notion.domain.models

data class SpacedRepetitionDataBaseGroup(
    val group: List<SpacedRepetitionDataBase>
) {
    val totalFlashCardsNeedRevising: Int
        get() = group.flatMap { it.flashCards }
            .count()
}