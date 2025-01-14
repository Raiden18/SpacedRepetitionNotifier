package org.danceofvalkyries.telegram.impl.client

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
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


    override suspend fun getUpdates(): Flow<UpdateResponseData> {
        return channelFlow {
            embeddedServer(Netty, port = 8080) {
                routing {
                    post("/webhook") {
                        send(call.receiveText())
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }.start(wait = true)
        }.map { gson.fromJson(it, UpdateResponseData::class.java) }
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