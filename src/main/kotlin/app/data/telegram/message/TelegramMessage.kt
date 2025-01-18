package org.danceofvalkyries.app.data.telegram.message

import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.sendMessage

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
            data class Text(override val value: String) : Action(value)
        }

        interface Callback {
            val id: String
            val action: Action
            val messageId: Long
            suspend fun answer()
        }
    }
}

suspend fun TelegramMessage.sendTo(telegramChat: TelegramChat): TelegramMessage {
    return telegramChat.sendMessage(this)
}

suspend fun TelegramMessage.edit(
    newMessage: TelegramMessage,
    telegramChat: TelegramChat,
): TelegramMessage {
    return telegramChat.edit(
        messageId = id,
        newText = newMessage.text,
        newNestedButtons = newMessage.nestedButtons,
    )
}

suspend fun TelegramMessage.deleteFrom(telegramChat: TelegramChat) {
    telegramChat.delete(id)
}
