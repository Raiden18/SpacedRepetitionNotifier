package org.danceofvalkyries.telegram.domain.models

import org.danceofvalkyries.notion.domain.models.ImageUrl

data class TelegramMessageBody(
    val text: String,
    val nestedButtons: List<List<Button>>,
    val imageUrl: ImageUrl?,
)

fun TelegramMessageBody(
    text: String,
    buttons: List<Button>,
    imageUrl: ImageUrl?
): TelegramMessageBody {
    return TelegramMessageBody(
        text = text,
        nestedButtons = buttons.map { listOf(it) },
        imageUrl = imageUrl,
    )
}
