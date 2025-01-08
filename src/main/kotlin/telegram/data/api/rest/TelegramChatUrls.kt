package org.danceofvalkyries.telegram.data.api.rest

import okhttp3.HttpUrl

data class TelegramChatUrls(
    private val chatId: String,
    private val apiKey: String
) {

    private val telegramApiUrl: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host("api.telegram.org")
            .addPathSegment("bot$apiKey")
            .build()

    fun sendMessage(): HttpUrl = telegramApiUrl
        .newBuilder()
        .addPathSegment("sendMessage")
        .build()

    fun getUpdates(limit: Int): HttpUrl = telegramApiUrl
        .newBuilder()
        .addPathSegment("getUpdates")
        .addQueryParameter("limit", limit.toString())
        .build()

    fun deleteMessage(messageId: Long): HttpUrl = telegramApiUrl
        .newBuilder()
        .addPathSegment("deleteMessage")
        .addQueryParameter("chat_id", chatId)
        .addQueryParameter("message_id", messageId.toString())
        .build()

    fun editMessageText(): HttpUrl = telegramApiUrl
        .newBuilder()
        .addPathSegment("editMessageText")
        .build()
}