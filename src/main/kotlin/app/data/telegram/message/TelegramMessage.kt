package org.danceofvalkyries.app.data.telegram.message

interface TelegramMessage {
    val id: Long
    val text: String
    val imageUrl: String?
    val nestedButtons: List<List<Button>>

    interface Button {
        val text: String
        val action: Action

        sealed class Action(open val value: String) {
            data class Url(override val value: String) : Action(value)
            data class CallBackData(override val value: String) : Action(value)
        }

        interface Callback {
            val id: String
            val action: Action

            suspend fun answer()
        }
    }

    fun edit(
        newText: String,
        newImageUrl: String?,
        newNestedButtons: List<List<Button>>,
    ): TelegramMessage
}

