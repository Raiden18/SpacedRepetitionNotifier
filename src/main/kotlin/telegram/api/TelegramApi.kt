package org.danceofvalkyries.telegram.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.json.toRequestBody
import org.danceofvalkyries.telegram.api.rest.request.bodies.SendMessageBody

interface TelegramApi {
    suspend fun sendMessage(
        chatId: String,
        text: String,
    )
}

class TelegramApiImpl(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val apiKey: String,
) : TelegramApi {

    override suspend fun sendMessage(
        chatId: String,
        text: String,
    ) {
        val body = SendMessageBody(
            gson = gson,
            chatId = chatId,
            text = text
        ).toRequestBody()
        val post = Request.Builder()
            .url("https://api.telegram.org/bot$apiKey/sendMessage")
            .post(body)
            .build()
        client.newCall(post).execute()
    }
}