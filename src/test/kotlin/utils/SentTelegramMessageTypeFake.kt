package utils

import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessageType

data class SentTelegramMessageTypeFake(
    override val id: Long,
    override val type: String
) : SentTelegramMessageType {

    override val text: String
        get() = TODO("Not yet implemented")
    override val imageUrl: String?
        get() = TODO("Not yet implemented")
    override val nestedButtons: List<List<TelegramMessage.Button>>
        get() = TODO("Not yet implemented")
}