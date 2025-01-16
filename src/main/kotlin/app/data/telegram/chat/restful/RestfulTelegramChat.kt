package org.danceofvalkyries.app.data.telegram.chat.restful

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
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.jsonobjects.TelegramChatUrls
import org.danceofvalkyries.app.data.telegram.jsons.*
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message.restful.RestfulTelegramMessage
import org.danceofvalkyries.app.data.telegram.message.restful.RestfulTelegramUpdateMessageButtonCallback
import org.danceofvalkyries.utils.rest.parse
import org.danceofvalkyries.utils.rest.post
import org.danceofvalkyries.utils.rest.request

class RestfulTelegramChat(
    private val apiKey: String,
    private val client: OkHttpClient,
    private val gson: Gson,
    private val chatId: String,
) : TelegramChat {

    private companion object {
        val NOT_SUPPORTED_BY_TELEGRAM_TAGS = listOf(
            "shutterstock.com",
            "base64",
            "?",
        )

        const val BLUE_SCREEN =
            "https://neosmart.net/wiki/wp-content/uploads/sites/5/2013/08/unmountable-boot-volume.png"
    }

    private val telegramChatUrls: TelegramChatUrls
        get() = TelegramChatUrls(
            apiKey = apiKey
        )

    override suspend fun sendTextMessage(
        text: String,
        nestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage {
        val requestBody = MessageData(
            chatId = chatId,
            text = text,
            parseMode = "MarkdownV2",
            replyMarkup = ReplyMarkupData(
                nestedButtons.map {
                    it.map { button ->
                        ButtonData(
                            text = button.text,
                            callbackData = button.action.value.takeIf { button.action is TelegramMessage.Button.Action.CallBackData },
                            url = button.action.value.takeIf { button.action is TelegramMessage.Button.Action.Url }
                        )
                    }
                }
            )
        )

        val response = sendMessage(telegramChatUrls.sendMessage(), requestBody)
        return RestfulTelegramMessage(
            chatId = chatId,
            client = client,
            gson = gson,
            telegramChatUrls = telegramChatUrls,
            id = response.messageId!!,
            text = text,
            imageUrl = null,
            nestedButtons = nestedButtons
        )
    }

    override suspend fun sendPhotoMessage(
        caption: String,
        imageUrl: String,
        nestedButtons: List<List<TelegramMessage.Button>>
    ): TelegramMessage {
        val isSupportedByTelegram = NOT_SUPPORTED_BY_TELEGRAM_TAGS.any { imageUrl.contains(it) }.not()
        val photo = if (isSupportedByTelegram) imageUrl else BLUE_SCREEN
        val requestBody = MessageData(
            chatId = chatId,
            caption = caption,
            photo = photo,
            parseMode = "MarkdownV2",
            replyMarkup = ReplyMarkupData(
                nestedButtons.map {
                    it.map { button ->
                        ButtonData(
                            text = button.text,
                            callbackData = button.action.value.takeIf { button.action is TelegramMessage.Button.Action.CallBackData },
                            url = button.action.value.takeIf { button.action is TelegramMessage.Button.Action.Url }
                        )
                    }
                }
            )
        )
        val response = sendMessage(telegramChatUrls.sendPhoto(), requestBody)
        return RestfulTelegramMessage(
            chatId = chatId,
            client = client,
            gson = gson,
            telegramChatUrls = telegramChatUrls,
            id = response.messageId!!,
            text = caption,
            imageUrl = photo,
            nestedButtons = nestedButtons
        )
    }

    override suspend fun delete(messageId: Long) {
        Request.Builder()
            .url(telegramChatUrls.deleteMessage(messageId, chatId.toLong()))
            .get()
            .build()
            .request(client)
    }

    override suspend fun getMessage(messageId: Long): TelegramMessage {
        return RestfulTelegramMessage(
            chatId = chatId,
            client = client,
            gson = gson,
            telegramChatUrls = telegramChatUrls,
            id = messageId,
            text = "",
            imageUrl = null,
            nestedButtons = emptyList()
        )
    }

    override fun getEvents(): Flow<TelegramMessage.Button.Callback> {
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
                RestfulTelegramUpdateMessageButtonCallback(
                    id = it.callbackQueryData.id,
                    action = TelegramMessage.Button.Action.CallBackData(it.callbackQueryData.data!!),
                    gson = gson,
                    client = client,
                    telegramChatUrls = telegramChatUrls,
                )
            }

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