package org.danceofvalkyries.app.data.telegram.message.restful

import com.google.gson.Gson
import org.danceofvalkyries.app.data.telegram.jsonobjects.TelegramChatUrls
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.utils.HttpClient
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