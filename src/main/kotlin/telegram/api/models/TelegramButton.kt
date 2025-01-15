package org.danceofvalkyries.telegram.api.models

data class TelegramButton(
    val text: String,
    val action: Action
) {
    sealed class Action(open val value: String) {
        data class Url(override val value: String) : Action(value)
        data class CallBackData(override val value: String) : Action(value)
    }
}
