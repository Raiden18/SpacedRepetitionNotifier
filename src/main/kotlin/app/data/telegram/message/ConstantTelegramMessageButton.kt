package org.danceofvalkyries.app.data.telegram.message

data class ConstantTelegramMessageButton(
    override val text: String,
    override val action: TelegramMessage.Button.Action
) : TelegramMessage.Button