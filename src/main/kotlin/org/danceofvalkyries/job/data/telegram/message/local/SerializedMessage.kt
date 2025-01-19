package org.danceofvalkyries.job.data.telegram.message.local

import org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessageType

class SerializedMessage(
    override val id: Long,
    override val type: String,
) : LocalTelegramMessage(), SentTelegramMessageType {

    override val text: String
        get() = error("Text was not saved")
}