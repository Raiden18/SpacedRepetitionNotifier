package org.danceofvalkyries.telegram.data.api.mappers

import org.danceofvalkyries.telegram.domain.TelegramMessageBody

fun TelegramMessageBody.textWithEscapedCharacters(): String {
    return text.replace("!", "\\!")
        .replace("(", "\\(")
        .replace(")", "\\)")
        .replace("=", "\\=")
        .replace(".", "\\.")

}