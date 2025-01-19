package org.danceofvalkyries.job.data.telegram.message.local

class SerializedMessage(
    override val id: Long,
    override val type: String,
) : LocalTelegramMessage(), org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessageType {

    override val text: String
        get() = error("Text was not saved")
}