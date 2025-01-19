package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.telegram.message_types.SentTelegramMessageType

data class SerializedMessage(
    private val id: Long,
    override val type: String,
) : LocalTelegramMessage(), SentTelegramMessageType {

    override fun getId(): Long {
        return id
    }

    override fun getText(): String = error("No text in serialized message")
}