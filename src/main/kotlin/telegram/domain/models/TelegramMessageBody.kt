package org.danceofvalkyries.telegram.domain.models

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup

data class TelegramMessageBody(
    val text: String,
    val nestedButtons: List<List<Button>>,
    val photoUrl: String? = null,
)

fun TelegramMessageBody(
    text: String,
    buttons: List<Button>,
): TelegramMessageBody {
    return TelegramMessageBody(
        text = text,
        nestedButtons = buttons.map { listOf(it) }
    )
}
