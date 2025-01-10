package org.danceofvalkyries.telegram.data.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.json.parse
import org.danceofvalkyries.json.post
import org.danceofvalkyries.json.request
import org.danceofvalkyries.telegram.data.api.rest.TelegramChatUrls
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.*
import org.danceofvalkyries.telegram.data.api.rest.response.TelegramMessageRootResponse
import org.danceofvalkyries.telegram.domain.TelegramMessage
import org.danceofvalkyries.telegram.domain.TelegramMessageBody

interface TelegramChatApi {
    suspend fun sendMessage(textBody: TelegramMessageBody): TelegramMessage
    suspend fun deleteMessage(id: Long)
    suspend fun editMessageText(messageId: Long, text: String)
}
