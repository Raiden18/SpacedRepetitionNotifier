package org.danceofvalkyries.app.data.telegram.chat.restful

import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage.Button.Action

class RestfulTelegramButton(
    override val text: String,
    override val action: Action,
) : TelegramMessage.Button