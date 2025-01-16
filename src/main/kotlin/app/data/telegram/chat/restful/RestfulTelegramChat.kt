package org.danceofvalkyries.app.data.telegram.chat.restful

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.telegram.impl.TelegramChatUrls
import org.danceofvalkyries.telegram.impl.models.ButtonData
import org.danceofvalkyries.telegram.impl.models.MessageData
import org.danceofvalkyries.telegram.impl.models.ReplyMarkupData
import org.danceofvalkyries.telegram.impl.models.TelegramMessageRootResponse
import org.danceofvalkyries.utils.rest.jsonObject
import org.danceofvalkyries.utils.rest.parse
import org.danceofvalkyries.utils.rest.post
import org.danceofvalkyries.utils.rest.request

class RestfulTelegramChat(
    private val apiKey: String,
    private val client: OkHttpClient,
    private val gson: Gson,
    private val chatId: String,
) : TelegramChat {

    private val telegramChatUrls: TelegramChatUrls
        get() = TelegramChatUrls(
            apiKey = apiKey
        )

    override suspend fun sendTextMessage(
        text: String,
        nestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage {
        val requestBody = MessageData(
            chatId = chatId,
            text = text,
            parseMode = "MarkdownV2",
            replyMarkup = ReplyMarkupData(
                nestedButtons.map {
                    it.map { button ->
                        ButtonData(
                            text = button.text,
                            callbackData = button.action.value.takeIf { button.action is TelegramMessage.Button.Action.CallBackData },
                            url = button.action.value.takeIf { button.action is TelegramMessage.Button.Action.Url }
                        )
                    }
                }
            )
        )
        val response = sendMessage(telegramChatUrls.sendMessage(), requestBody)
        return RestfulTelegramMessage(
            chatId = chatId,
            client = client,
            gson = gson,
            telegramChatUrls = telegramChatUrls,
            id = response.messageId!!,
            text = text,
            imageUrl = null,
            nestedButtons = nestedButtons
        )
    }

    override suspend fun sendPhotoMessage(
        caption: String,
        imageUrl: String,
        nestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage {
        val requestBody = MessageData(
            chatId = chatId,
            caption = caption,
            photo = imageUrl,
            parseMode = "MarkdownV2",
            replyMarkup = ReplyMarkupData(
                nestedButtons.map {
                    it.map { button ->
                        ButtonData(
                            text = button.text,
                            callbackData = button.action.value.takeIf { button.action is TelegramMessage.Button.Action.CallBackData },
                            url = button.action.value.takeIf { button.action is TelegramMessage.Button.Action.Url }
                        )
                    }
                }
            )
        )
        val response = sendMessage(telegramChatUrls.sendPhoto(), requestBody)
        return RestfulTelegramMessage(
            chatId = chatId,
            client = client,
            gson = gson,
            telegramChatUrls = telegramChatUrls,
            id = response.messageId!!,
            text = caption,
            imageUrl = imageUrl,
            nestedButtons = nestedButtons
        )
    }

    override suspend fun delete(messageId: Long) {
        Request.Builder()
            .url(telegramChatUrls.deleteMessage(messageId, chatId.toLong()))
            .get()
            .build()
            .request(client)
    }

    override suspend fun getMessage(messageId: Long): TelegramMessage {
        return RestfulTelegramMessage(
            chatId = chatId,
            client = client,
            gson = gson,
            telegramChatUrls = telegramChatUrls,
            id = messageId,
            text = "",
            imageUrl = null,
            nestedButtons = emptyList()
        )
    }

    override suspend fun answerCallBack(
        callBackId: String
    ) {
        Request.Builder()
            .url(telegramChatUrls.answerCallback())
            .post(
                jsonObject {
                    "callback_query_id" to callBackId
                }.let(gson::toJson)
            ).build()
            .request(client)
    }

    private fun sendMessage(url: HttpUrl, textBody: MessageData): MessageData {
        val request = Request.Builder()
            .url(url)
            .post(gson.toJson(textBody))
            .build()
        return request.request(client)
            .parse<TelegramMessageRootResponse>(gson)
            .result
    }

}