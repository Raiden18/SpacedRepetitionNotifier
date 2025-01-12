package org.danceofvalkyries.notion.domain.models

data class KnowLevels(
    val levels: Map<Int, Boolean>,
) {

    fun disableAll(): KnowLevels {
        return copy(levels.mapValues { false })
    }

    fun next(): KnowLevels {
        val nextChecked = levels.keys.firstOrNull { levels[it] == false } ?: return this
        return copy(
            levels = levels.mapValues { it.key <= nextChecked }
        )
    }
}