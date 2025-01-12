package org.danceofvalkyries.notion.domain.models.text

data class Text(
    private val value: String?
) {

    companion object {
        val EMPTY = Text("")
    }

    fun getValue(textFormatter: TextFormatter = TextFormatter()): String? {
        return textFormatter.format(value)
    }
}