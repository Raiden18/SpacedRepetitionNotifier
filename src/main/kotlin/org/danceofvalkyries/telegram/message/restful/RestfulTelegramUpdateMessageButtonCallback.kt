package org.danceofvalkyries.telegram.message.restful

import com.google.gson.Gson
import org.danceofvalkyries.telegram.jsonobjects.TelegramChatUrls
import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.utils.rest.clients.http.HttpClient
import org.danceofvalkyries.utils.rest.jsonObject

class RestfulTelegramUpdateMessageButtonCallback(
    override val id: String,
    override val action: TelegramMessage.Button.Action,
    override val messageId: Long,
    private val telegramChatUrls: TelegramChatUrls,
    private val gson: Gson,
    private val httpClient: HttpClient,
) : TelegramMessage.Button.Callback {

    override suspend fun answer() {
        httpClient.post(
            url = telegramChatUrls.answerCallback().toString(),
            body = jsonObject { "callback_query_id" to id }.let(gson::toJson),
            headers = emptyList()
        )

    }
}