package org.danceofvalkyries.telegram.data.api

import org.danceofvalkyries.notion.domain.models.text.TextFormatter


class TelegramFriendlyTextFormatter : TextFormatter {

    override fun format(string: String?): String? {
        if (string == null) return null
        return string.replace("!", "\\!")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("=", "\\=")
            .replace(".", "\\.")
            .replace("_", "\\_")
            .replace("-", "\\-")
            .replace("\\\\", "\\")
    }
}