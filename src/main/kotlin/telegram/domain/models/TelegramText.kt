package org.danceofvalkyries.telegram.domain.models

data class TelegramText(
    private val value: String
) {

    companion object {
        val EMPTY = TelegramText("")
    }

    fun get(): String {
        return value.replace("!", "\\!")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("=", "\\=")
            .replace(".", "\\.")
            .replace("_", "\\_")
            .replace("-", "\\-")
            .replace("\\\\", "\\")
    }
}