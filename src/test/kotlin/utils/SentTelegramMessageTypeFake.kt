package utils

import org.danceofvalkyries.job.data.telegram.message.TelegramMessage
import org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessageType

data class SentTelegramMessageTypeFake(
    override val id: Long,
    override val type: String
) : org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessageType {

    override val text: String
        get() = TODO("Not yet implemented")
    override val imageUrl: String?
        get() = TODO("Not yet implemented")
    override val nestedButtons: List<List<TelegramMessage.Button>>
        get() = TODO("Not yet implemented")
}