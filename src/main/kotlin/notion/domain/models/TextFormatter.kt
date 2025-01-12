package org.danceofvalkyries.notion.domain.models

fun TextFormatter(): TextFormatter {
    return object : TextFormatter {
        override fun format(string: String?): String? {
            return string
        }
    }
}

interface TextFormatter {
    fun format(string: String?): String?
}