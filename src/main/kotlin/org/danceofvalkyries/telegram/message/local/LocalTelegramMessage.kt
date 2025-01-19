package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.telegram.message.TelegramMessage

abstract class LocalTelegramMessage : TelegramMessage {

    abstract val type: String

    override fun getId(): Long {
        error("No id in Local Message")
    }

    override fun getImageUrl(): String? {
        return null
    }

    override fun getNestedButtons(): List<List<TelegramMessage.Button>> {
        return emptyList()
    }
}