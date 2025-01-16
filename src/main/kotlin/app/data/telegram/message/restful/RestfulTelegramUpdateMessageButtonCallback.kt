package org.danceofvalkyries.app.data.telegram.message.restful

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.app.data.telegram.jsonobjects.TelegramChatUrls
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.utils.rest.jsonObject
import org.danceofvalkyries.utils.rest.post
import org.danceofvalkyries.utils.rest.request

class RestfulTelegramUpdateMessageButtonCallback(
    override val id: String,
    override val action: TelegramMessage.Button.Action,
    private val telegramChatUrls: TelegramChatUrls,
    private val gson: Gson,
    private val client: OkHttpClient,
) : TelegramMessage.Button.Callback {

    override suspend fun answer() {
        Request.Builder()
            .url(telegramChatUrls.answerCallback())
            .post(jsonObject { "callback_query_id" to id }.let(gson::toJson)).build()
            .request(client)

    }
}