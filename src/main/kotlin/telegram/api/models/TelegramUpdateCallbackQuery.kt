package org.danceofvalkyries.telegram.api.models

data class TelegramUpdateCallbackQuery(
    val id: String,
    val callback: TelegramButton.Action.CallBackData
)