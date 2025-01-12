package org.danceofvalkyries.telegram.data.api

import org.danceofvalkyries.app.domain.models.text.Text


class TelegramFriendlyTextModifier : Text.TextModifier {

    override fun modify(value: String?): String? {
        if (value == null) return null
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