package org.danceofvalkyries.telegram.data.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.json.parse
import org.danceofvalkyries.json.post
import org.danceofvalkyries.json.request
import org.danceofvalkyries.telegram.data.api.mappers.textWithEscapedCharacters
import org.danceofvalkyries.telegram.data.api.rest.TelegramChatUrls
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.ButtonRequest
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.EditMessageBody
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.ReplyMarkupResponse
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.SendMessageRequest
import org.danceofvalkyries.telegram.data.api.rest.response.TelegramMessageRootResponse
import org.danceofvalkyries.telegram.domain.TelegramMessage
import org.danceofvalkyries.telegram.domain.TelegramMessageBody

class TelegramChatApiImpl(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val apiKey: String,
    private val chatId: String,
) : TelegramChatApi {

    private val telegramChatUrls: TelegramChatUrls
        get() = TelegramChatUrls(
            apiKey = apiKey,
            chatId = chatId,
        )

    override suspend fun sendMessage(textBody: TelegramMessageBody): TelegramMessage {
        val request = Request.Builder()
            .url(telegramChatUrls.sendMessage())
            .post(
                gson.toJson(
                    SendMessageRequest(
                        chatId = chatId,
                        text = textBody.textWithEscapedCharacters(),
                        parseMode = "MarkdownV2",
                        replyMarkup = ReplyMarkupResponse(
                            textBody.nestedButtons.map {
                                it.map {
                                    ButtonRequest(
                                        text = it.text,
                                        callbackData = "1",
                                        url = it.url
                                    )
                                }
                            }
                        )
                    )
                )
            ).build()

        println(request.request(client).body?.string())

        val response = request.request(client)
            .parse<TelegramMessageRootResponse>(gson)
            .result


        return TelegramMessage(
            id = response.messageId,
            body = TelegramMessageBody(
                text = response.text,
                buttons = emptyList()
            ),
        )
    }

    override suspend fun deleteMessage(id: Long) {
        Request.Builder()
            .url(telegramChatUrls.deleteMessage(id))
            .get()
            .build()
            .request(client)
    }

    override suspend fun editMessageText(messageId: Long, text: String) {
        Request.Builder()
            .url(telegramChatUrls.editMessageText())
            .post(
                EditMessageBody(
                    gson = gson,
                    chatId = chatId,
                    text = text,
                    messageId = messageId.toString()
                )
            )
            .build()
            .request(client)
    }

    override suspend fun sendPhoto(): TelegramMessage {
        TODO()
    }
}