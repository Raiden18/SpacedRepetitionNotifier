package org.danceofvalkyries.telegram.message.restful

import org.danceofvalkyries.telegram.message.TelegramMessage

class RestfulTelegramMessage(
    override val id: Long,
    override val text: String,
    override val imageUrl: String?,
    override val nestedButtons: List<List<TelegramMessage.Button>>,
) : TelegramMessage