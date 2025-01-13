package org.danceofvalkyries.telegram.impl.client

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.telegram.impl.client.models.CallbackQueryResult
import org.danceofvalkyries.telegram.impl.client.models.MessageData
import org.danceofvalkyries.telegram.impl.client.models.TelegramMessageRootResponse
import org.danceofvalkyries.telegram.impl.client.models.UpdateResponseData
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

    override suspend fun sendMessage(messageData: MessageData): MessageData {
        return sendMessage(telegramChatUrls.sendMessage(), messageData)
    }

    override suspend fun deleteMessage(messageId: Long, chatId: Long) {
        Request.Builder()
            .url(telegramChatUrls.deleteMessage(messageId, chatId))
            .get()
            .build()
            .request(client)
    }

    override suspend fun editMessageText(messageId: Long, text: MessageData) {
        Request.Builder()
            .url(telegramChatUrls.editMessageText())
            .post(gson.toJson(text))
            .build()
            .request(client)
    }

    override suspend fun sendPhoto(messageData: MessageData): MessageData {
        return sendMessage(telegramChatUrls.sendPhoto(), messageData)
    }


    override suspend fun getUpdates(): List<UpdateResponseData> {
        return Request.Builder()
            .url(telegramChatUrls.getUpdates())
            .get()
            .build()
            .request(client)
            .parse<CallbackQueryResult>(gson)
            .updateResponseData
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