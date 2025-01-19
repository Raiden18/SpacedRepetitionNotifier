package org.danceofvalkyries.job.data.telegram.message.local

import org.danceofvalkyries.job.data.telegram.message.TelegramMessage

abstract class LocalTelegramMessage : TelegramMessage {

    abstract val type: String

    override val id: Long
        get() = error("No id in Local Message")

    override val imageUrl: String?
        get() = null

    override val nestedButtons: List<List<TelegramMessage.Button>>
        get() = emptyList()
}