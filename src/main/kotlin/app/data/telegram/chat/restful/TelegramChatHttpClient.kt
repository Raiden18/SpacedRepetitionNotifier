package org.danceofvalkyries.app.data.telegram.chat.restful

import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.utils.HttpClient
import org.danceofvalkyries.utils.HttpClient.Response
import org.danceofvalkyries.utils.rest.request

class TelegramChatHttpClient(
    private val okHttpClient: OkHttpClient,
) : HttpClient {

    override fun get(url: String): Response {
        val response = Request.Builder()
            .url(url)
            .get()
            .build()
            .request(okHttpClient)
        return Response(
            requestUrl = response.request.url.toString()
        )
    }
}