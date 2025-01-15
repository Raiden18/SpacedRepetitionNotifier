package utils

import org.danceofvalkyries.app.domain.telegram.TelegramMessage

data class TelegramMessageFake(
    override val id: Long,
    override val type: String
) : TelegramMessage