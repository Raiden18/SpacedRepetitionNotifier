package org.danceofvalkyries.telegram.api.models

data class TelegramMessageBody(
    val text: TelegramText,
    val nestedButtons: List<List<TelegramButton>>,
    val imageUrl: TelegramImageUrl?,
) {

    companion object {
        val EMPTY = TelegramMessageBody(
            text = TelegramText.EMPTY,
            nestedButtons = emptyList(),
            imageUrl = null,
        )
    }
}

fun TelegramMessageBody(
    text: String,
    telegramButtons: List<TelegramButton>,
    telegramImageUrl: TelegramImageUrl?,
): TelegramMessageBody {
    return TelegramMessageBody(
        text = TelegramText(text),
        nestedButtons = telegramButtons.map { listOf(it) },
        imageUrl = telegramImageUrl,
    )
}
