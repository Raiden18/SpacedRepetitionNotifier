package utils

import org.danceofvalkyries.telegram.message.TelegramMessage

data class SentTelegramMessageTypeFake(
    override val id: Long,
    override val type: String
) : org.danceofvalkyries.telegram.message_types.SentTelegramMessageType {

    override val text: String
        get() = TODO("Not yet implemented")
    override val imageUrl: String?
        get() = TODO("Not yet implemented")
    override val nestedButtons: List<List<TelegramMessage.Button>>
        get() = TODO("Not yet implemented")
}