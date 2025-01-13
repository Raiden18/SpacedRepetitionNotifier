package org.danceofvalkyries.telegram.impl.restapi

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.telegram.impl.restapi.response.TelegramMessageResponse
import org.danceofvalkyries.telegram.impl.restapi.response.TelegramMessageRootResponse
import org.danceofvalkyries.telegram.impl.restapi.request.bodies.SendMessageRequest
import org.danceofvalkyries.utils.rest.parse
import org.danceofvalkyries.utils.rest.post
import org.danceofvalkyries.utils.rest.request

class TelegramChatRestApiImpl(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val apiKey: String,
) : TelegramChatRestApi {

    private val telegramChatUrls: TelegramChatUrls
        get() = TelegramChatUrls(
            apiKey = apiKey
        )

    override suspend fun sendMessage(textBody: SendMessageRequest): TelegramMessageResponse {
        return sendMessage(telegramChatUrls.sendMessage(), textBody)
    }

    override suspend fun deleteMessage(messageId: Long, chatId: Long) {
        Request.Builder()
            .url(telegramChatUrls.deleteMessage(messageId, chatId))
            .get()
            .build()
            .request(client)
    }

    override suspend fun editMessageText(messageId: Long, text: SendMessageRequest) {
        Request.Builder()
            .url(telegramChatUrls.editMessageText())
            .post(gson.toJson(text))
            .build()
            .request(client)
    }

    override suspend fun sendPhoto(textBody: SendMessageRequest): TelegramMessageResponse {
        return sendMessage(telegramChatUrls.sendPhoto(), textBody)
    }

    override suspend fun getUpdate() {
        Request.Builder()
            .url(telegramChatUrls.getUpdates())
            .build()
            .request(client)
    }

    private fun sendMessage(url: HttpUrl, textBody: SendMessageRequest): TelegramMessageResponse {
        val request = Request.Builder()
            .url(url)
            .post(gson.toJson(textBody))
            .build()

        return request.request(client)
            .parse<TelegramMessageRootResponse>(gson)
            .result
    }
}