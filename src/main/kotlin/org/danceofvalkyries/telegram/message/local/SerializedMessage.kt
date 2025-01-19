package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.telegram.message_types.SentTelegramMessageType

class SerializedMessage(
    override val id: Long,
    override val type: String,
) : LocalTelegramMessage(), org.danceofvalkyries.telegram.message_types.SentTelegramMessageType {

    override val text: String
        get() = error("Text was not saved")
}