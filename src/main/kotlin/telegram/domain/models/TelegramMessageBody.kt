package org.danceofvalkyries.telegram.domain.models

import org.danceofvalkyries.app.domain.models.ImageUrl
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody.Type

data class TelegramMessageBody(
    val text: String,
    val nestedButtons: List<List<Button>>,
    val imageUrl: ImageUrl?,
    val type: Type,
) {
    enum class Type {
        NOTIFICATION,
        FLASH_CARD,
        UNKNOWN;
    }

    companion object {
        val EMPTY = TelegramMessageBody(
            text = "",
            nestedButtons = emptyList(),
            imageUrl = null,
            type = Type.UNKNOWN,
        )
    }
}

fun TelegramMessageBody(
    text: String,
    buttons: List<Button>,
    imageUrl: ImageUrl?,
    type: Type,
): TelegramMessageBody {
    return TelegramMessageBody(
        text = text,
        nestedButtons = buttons.map { listOf(it) },
        imageUrl = imageUrl,
        type = type,
    )
}
