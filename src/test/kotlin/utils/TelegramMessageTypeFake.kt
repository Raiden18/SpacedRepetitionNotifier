package utils

import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessageType

data class TelegramMessageTypeFake(
    override val id: Long,
    override val type: String
) : TelegramMessageType