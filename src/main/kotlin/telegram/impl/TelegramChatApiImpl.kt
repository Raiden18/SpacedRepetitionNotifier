package org.danceofvalkyries.telegram.impl

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.telegram.api.TelegramChatApi
import org.danceofvalkyries.telegram.api.models.*
import org.danceofvalkyries.telegram.impl.models.MessageData
import org.danceofvalkyries.telegram.impl.models.TelegramMessageRootResponse
import org.danceofvalkyries.telegram.impl.models.UpdateResponseData
import org.danceofvalkyries.utils.rest.jsonObject
import org.danceofvalkyries.utils.rest.parse
import org.danceofvalkyries.utils.rest.post
import org.danceofvalkyries.utils.rest.request

class TelegramChatApiImpl(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val apiKey: String,
    private val chatId: String,
) : TelegramChatApi {

    private val telegramChatUrls: TelegramChatUrls
        get() = TelegramChatUrls(
            apiKey = apiKey
        )

    override suspend fun sendTextMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage {
        val request = telegramMessageBody.toRequest(chatId)
        val response = sendMessage(telegramChatUrls.sendMessage(), request)
        return TelegramMessage(
            id = response.messageId!!,
            body = telegramMessageBody
        )
    }

    override suspend fun sendPhotoMessage(telegramMessageBody: TelegramMessageBody): TelegramMessage {
        val request = telegramMessageBody.toRequest(chatId)
        val response = sendMessage(telegramChatUrls.sendPhoto(), request)
        return TelegramMessage(
            id = response.messageId!!,
            body = telegramMessageBody
        )
    }

    override suspend fun deleteFromChat(messageId: Long) {
        Request.Builder()
            .url(telegramChatUrls.deleteMessage(messageId, chatId.toLong()))
            .get()
            .build()
            .request(client)
    }

    override suspend fun editInChat(telegramMessageBody: TelegramMessageBody, messageId: Long) {
        val request = telegramMessageBody.toRequest(
            chatId = chatId,
            messageId = messageId,
        )
        Request.Builder()
            .url(telegramChatUrls.editMessageText())
            .post(gson.toJson(request))
            .build()
            .request(client)
    }

    override suspend fun getUpdates(): Flow<TelegramUpdate> {
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
            .map {
                TelegramUpdate(
                    id = it.id,
                    telegramUpdateCallbackQuery = TelegramUpdateCallbackQuery(
                        id = it.callbackQueryData.id,
                        message = it.callbackQueryData.message!!.toDomain(),
                        callback = TelegramButton.Action.CallBackData(it.callbackQueryData.data!!),
                    ),
                )
            }
    }

    override suspend fun answerCallbackQuery(
        telegramUpdateCallbackQuery: TelegramUpdateCallbackQuery
    ) {
        Request.Builder()
            .url(telegramChatUrls.answerCallback())
            .post(
                jsonObject {
                    "callback_query_id" to telegramUpdateCallbackQuery.id
                }.let(gson::toJson)
            ).build()
            .request(client)
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