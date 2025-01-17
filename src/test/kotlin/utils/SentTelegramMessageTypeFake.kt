package utils

import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessageType

class SentTelegramMessageTypeFake(override val id: Long, override val type: String) : SentTelegramMessageType