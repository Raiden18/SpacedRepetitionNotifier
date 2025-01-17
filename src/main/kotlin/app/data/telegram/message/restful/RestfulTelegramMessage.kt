package org.danceofvalkyries.app.data.telegram.message.restful

import com.google.gson.Gson
import org.danceofvalkyries.app.data.telegram.jsonobjects.TelegramChatUrls
import org.danceofvalkyries.app.data.telegram.jsons.ButtonData
import org.danceofvalkyries.app.data.telegram.jsons.MessageData
import org.danceofvalkyries.app.data.telegram.jsons.ReplyMarkupData
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage.Button
import org.danceofvalkyries.utils.HttpClient

class RestfulTelegramMessage(
    private val chatId: String,
    private val client: HttpClient,
    private val gson: Gson,
    private val telegramChatUrls: TelegramChatUrls,
    override val id: Long,
    override val text: String,
    override val imageUrl: String?,
    override val nestedButtons: List<List<Button>>,
) : TelegramMessage {

    override fun edit(
        newText: String,
        newImageUrl: String?,
        newNestedButtons: List<List<Button>>,
    ): TelegramMessage {
        val request = MessageData(
            messageId = id,
            chatId = chatId,
            text = newText,
            parseMode = "MarkdownV2",
            replyMarkup = ReplyMarkupData(
                nestedButtons.map {
                    it.map { button ->
                        ButtonData(
                            text = button.text,
                            callbackData = button.action.value.takeIf { button.action is Button.Action.CallBackData },
                            url = button.action.value.takeIf { button.action is Button.Action.Url }
                        )
                    }
                }
            )
        )
        client.post(
            url = telegramChatUrls.editMessageText().toString(),
            body = gson.toJson(request),
            headers = emptyList()
        )
        return RestfulTelegramMessage(
            chatId = chatId,
            client = client,
            gson = gson,
            telegramChatUrls = telegramChatUrls,
            id = id,
            text = newText,
            imageUrl = newImageUrl,
            nestedButtons = newNestedButtons
        )
    }
}