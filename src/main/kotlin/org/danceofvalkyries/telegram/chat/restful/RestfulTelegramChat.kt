package org.danceofvalkyries.telegram.chat.restful

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.HttpUrl
import org.danceofvalkyries.job.data.telegram.jsonobjects.ButtonData
import org.danceofvalkyries.job.data.telegram.jsons.ReplyMarkupData
import org.danceofvalkyries.telegram.jsonobjects.UpdateResponseData
import org.danceofvalkyries.telegram.chat.TelegramChat
import org.danceofvalkyries.telegram.jsonobjects.MessageData
import org.danceofvalkyries.telegram.jsonobjects.TelegramChatUrls
import org.danceofvalkyries.telegram.jsonobjects.TelegramMessageRootResponse
import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.telegram.message.restful.RestfulTelegramMessage
import org.danceofvalkyries.telegram.message.restful.RestfulTelegramUpdateMessageButtonCallback
import org.danceofvalkyries.utils.rest.clients.http.HttpClient
import org.danceofvalkyries.utils.rest.clients.sever.SeverClient

class RestfulTelegramChat(
    private val apiKey: String,
    private val gson: Gson,
    private val chatId: String,
    private val severClient: SeverClient,
    httpClient: HttpClient,
) : TelegramChat {

    private val telegramChatUrls: TelegramChatUrls
        get() = TelegramChatUrls(
            apiKey = apiKey
        )

    private val httpClient = TelegramChatHttpClientDecorator(
        httpClient,
        gson,
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
            id = response.messageId!!,
            text = caption,
            imageUrl = imageUrl,
            nestedButtons = nestedButtons
        )
    }

    override suspend fun delete(messageId: Long) {
        val url = telegramChatUrls.deleteMessage(messageId, chatId.toLong()).toString()
        httpClient.get(
            url = url,
            headers = emptyList()
        )
    }

    override suspend fun edit(messageId: Long, newText: String, newNestedButtons: List<List<TelegramMessage.Button>>): TelegramMessage {
        val request = MessageData(
            messageId = messageId,
            chatId = chatId,
            text = newText,
            parseMode = "MarkdownV2",
            replyMarkup = ReplyMarkupData(
                newNestedButtons.map {
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
        httpClient.post(
            url = telegramChatUrls.editMessageText().toString(),
            body = gson.toJson(request),
            headers = emptyList()
        )
        return RestfulTelegramMessage(
            id = messageId,
            text = newText,
            imageUrl = null,
            nestedButtons = newNestedButtons
        )
    }

    override suspend fun getMessage(messageId: Long): TelegramMessage {
        return RestfulTelegramMessage(
            id = messageId,
            text = "",
            imageUrl = null,
            nestedButtons = emptyList()
        )
    }

    override fun getEvents(): Flow<TelegramMessage.Button.Callback> {
        return severClient.getWebHook()
            .map { gson.fromJson(it, UpdateResponseData::class.java) }
            .map {
                RestfulTelegramUpdateMessageButtonCallback(
                    id = it.callbackQueryData?.id.orEmpty(),
                    action = TelegramMessage.Button.Action.CallBackData(it.callbackQueryData?.data.orEmpty()),
                    gson = gson,
                    httpClient = httpClient,
                    telegramChatUrls = telegramChatUrls,
                    messageId = it.messageData?.messageId ?: -1
                )
            }

    }

    private fun sendMessage(url: HttpUrl, textBody: MessageData): MessageData {
        val response = httpClient.post(
            url = url.toString(),
            body = gson.toJson(textBody),
            headers = emptyList(),
        )
        return gson.fromJson(response.responseBody, TelegramMessageRootResponse::class.java)?.result ?: error("Message Data is NULL ${response.responseBody}")
    }
}