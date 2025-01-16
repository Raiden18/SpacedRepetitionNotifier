package org.danceofvalkyries.app.domain.message.notification

import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

data class DoneMessage(
    override val id: Long
) : NotificationMessage() {

    constructor() : this(-1)

    override suspend fun asTelegramBody(): TelegramMessageBody {
        return TelegramMessageBody(
            text = """Good Job! 😎 Everything is revised! ✅""",
            telegramButtons = emptyList(),
            telegramImageUrl = null,
        )
    }
}