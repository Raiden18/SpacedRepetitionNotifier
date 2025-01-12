package org.danceofvalkyries.telegram.data.api

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.json.parse
import org.danceofvalkyries.json.post
import org.danceofvalkyries.json.request
import org.danceofvalkyries.telegram.data.api.mappers.toRequest
import org.danceofvalkyries.telegram.data.api.rest.TelegramChatUrls
import org.danceofvalkyries.telegram.data.api.rest.response.TelegramMessageRootResponse
import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

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
        return sendMessage(telegramChatUrls.sendMessage(), textBody)
    }

    override suspend fun deleteMessage(id: Long) {
        Request.Builder()
            .url(telegramChatUrls.deleteMessage(id))
            .get()
            .build()
            .request(client)
    }

    override suspend fun editMessageText(messageId: Long, text: TelegramMessageBody) {
        Request.Builder()
            .url(telegramChatUrls.editMessageText())
            .post(gson.toJson(text.toRequest(chatId, messageId)))
            .build()
            .request(client)
    }

    override suspend fun sendPhoto(textBody: TelegramMessageBody): TelegramMessage {
        return sendMessage(telegramChatUrls.sendPhoto(), textBody)
    }

    override suspend fun getUpdate() {
        Request.Builder()
            .url(telegramChatUrls.getUpdates())
            .build()
            .request(client)
    }

    private fun sendMessage(url: HttpUrl, textBody: TelegramMessageBody): TelegramMessage {
        val request = Request.Builder()
            .url(url)
            .post(gson.toJson(textBody.toRequest(chatId)))
            .build()

        val response = request.request(client)
            .parse<TelegramMessageRootResponse>(gson)
            .result

        return TelegramMessage(
            id = response.messageId,
            body = textBody,
        )
    }
}