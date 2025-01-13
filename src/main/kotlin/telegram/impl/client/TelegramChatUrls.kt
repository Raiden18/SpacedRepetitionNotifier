package org.danceofvalkyries.telegram.impl.restapi

import okhttp3.HttpUrl

data class TelegramChatUrls(
    private val apiKey: String
) {

    private val telegramApiUrl: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host("api.telegram.org")
            .addPathSegment("bot$apiKey")
            .build()

    fun sendMessage(): HttpUrl = telegramEndpoint {
        addPathSegment("sendMessage")
    }

    fun getUpdates(limit: Int): HttpUrl = telegramEndpoint {
        addPathSegment("getUpdates")
        addQueryParameter("limit", limit.toString())
    }

    fun deleteMessage(messageId: Long, chatId: Long): HttpUrl = telegramEndpoint {
        addPathSegment("deleteMessage")
        addQueryParameter("chat_id", chatId.toString())
        addQueryParameter("message_id", messageId.toString())
    }

    fun editMessageText(): HttpUrl = telegramEndpoint {
        addPathSegment("editMessageText")
    }

    fun sendPhoto(): HttpUrl = telegramEndpoint {
        addPathSegment("sendPhoto")
    }

    fun getUpdates(): HttpUrl = telegramEndpoint {
        addPathSegment("getUpdates")
    }

    private fun telegramEndpoint(builder: HttpUrl.Builder.() -> Unit): HttpUrl {
        return telegramApiUrl.newBuilder()
            .apply(builder)
            .build()
    }
}