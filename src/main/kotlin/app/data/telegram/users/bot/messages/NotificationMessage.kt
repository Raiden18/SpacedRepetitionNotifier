package org.danceofvalkyries.app.data.telegram.users.bot.messages

import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.utils.resources.StringResources

class NotificationMessage(
    private val stringResources: StringResources,
    private val flashCardsCount: Int,
    override val nestedButtons: List<List<TelegramMessage.Button>>
) : LocalTelegramMessage() {

    override val text: String
        get() = """${stringResources.flashCardsToRevise(flashCardsCount)} 🧠""".trimIndent()
}