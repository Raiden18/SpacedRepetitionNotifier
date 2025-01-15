package org.danceofvalkyries.notion.api.models

data class KnowLevels(
    val levels: Map<Int, Boolean>,
) {

    companion object {
        val EMPTY = KnowLevels(emptyMap())
    }

    fun disableAll(): KnowLevels {
        return copy(levels.mapValues { false })
    }

    fun next(): KnowLevels {
        val nextChecked = levels.keys.firstOrNull { levels[it] == false } ?: return this
        return copy(
            levels = levels.mapValues { it.key <= nextChecked }
        )
    }

    fun isLevelChecked(level: Int): Boolean? {
        return levels[level]
    }
}