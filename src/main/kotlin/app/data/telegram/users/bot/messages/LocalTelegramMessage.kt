package org.danceofvalkyries.app.data.telegram.users.bot.messages

import org.danceofvalkyries.app.data.telegram.message.TelegramMessage

abstract class LocalTelegramMessage : TelegramMessage {
    override val id: Long
        get() = error("No id in")
    override val text: String
        get() = error("No text in")
    override val imageUrl: String?
        get() = null
    override val nestedButtons: List<List<TelegramMessage.Button>>
        get() = emptyList()
}