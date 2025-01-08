package org.danceofvalkyries.telegram.data.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.json.parse
import org.danceofvalkyries.json.post
import org.danceofvalkyries.json.request
import org.danceofvalkyries.telegram.data.api.rest.TelegramChatUrls
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.EditMessageBody
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.SendMessageBody
import org.danceofvalkyries.telegram.data.api.rest.response.TelegramMessageRootResponse
import org.danceofvalkyries.telegram.domain.TelegramMessage

interface TelegramChatApi {
    suspend fun sendMessage(text: String): TelegramMessage
    suspend fun deleteMessage(id: Long)
    suspend fun editMessageText(messageId: Long, text: String)
}

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

    override suspend fun sendMessage(text: String): TelegramMessage {
        val request = Request.Builder()
            .url(telegramChatUrls.sendMessage())
            .post(
                SendMessageBody(
                    gson = gson,
                    chatId = chatId,
                    text = text
                )
            )
            .build()

        val response = request.request(client)
            .parse<TelegramMessageRootResponse>(gson)
            .result

        return TelegramMessage(
            id = response.messageId,
            text = response.text,
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
}