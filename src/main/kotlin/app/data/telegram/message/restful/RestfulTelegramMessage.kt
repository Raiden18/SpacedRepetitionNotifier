package org.danceofvalkyries.app.data.telegram.message.restful

import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage.Button

class RestfulTelegramMessage(
    override val id: Long,
    override val text: String,
    override val imageUrl: String?,
    override val nestedButtons: List<List<Button>>,
) : TelegramMessage