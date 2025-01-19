package org.danceofvalkyries.telegram.message.restful

import org.danceofvalkyries.telegram.message.TelegramMessage

class RestfulTelegramMessage(
    private val id: Long,
    private val text: String,
    private val imageUrl: String?,
    private val nestedButtons: List<List<TelegramMessage.Button>>,
) : TelegramMessage {

    override fun getId(): Long {
        return id
    }

    override fun getText(): String {
        return text
    }

    override fun getImageUrl(): String? {
        return imageUrl
    }

    override suspend fun getNestedButtons(): List<List<TelegramMessage.Button>> {
        return nestedButtons
    }
}