package org.danceofvalkyries.utils.db

data class DataBasePaths(
    private val homeDirectory: String
) {

    fun development(): String {
        return getDbPath("_dev")
    }

    fun production(): String {
        return getDbPath("")
    }

    private fun getDbPath(postfix: String): String {
        return "$homeDirectory/spaced_repetition$postfix.db"
    }
}