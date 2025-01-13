package org.danceofvalkyries.telegram.domain.models

import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody.Type

data class TelegramMessageBody(
    val text: TelegramText,
    val nestedButtons: List<List<TelegramButton>>,
    val telegramImageUrl: TelegramImageUrl?,
    val type: Type,
) {

    // TODO: Probably must be part of app
    enum class Type {
        NOTIFICATION,
        FLASH_CARD,
        UNKNOWN;
    }

    companion object {
        val EMPTY = TelegramMessageBody(
            text = TelegramText.EMPTY,
            nestedButtons = emptyList(),
            telegramImageUrl = null,
            type = Type.UNKNOWN,
        )
    }
}

fun TelegramMessageBody(
    text: String,
    telegramButtons: List<TelegramButton>,
    telegramImageUrl: TelegramImageUrl?,
    type: Type,
): TelegramMessageBody {
    return TelegramMessageBody(
        text = TelegramText(text),
        nestedButtons = telegramButtons.map { listOf(it) },
        telegramImageUrl = telegramImageUrl,
        type = type,
    )
}
