package org.danceofvalkyries.job.data.telegram.chat.restful

import com.google.gson.Gson
import org.danceofvalkyries.job.data.telegram.chat.restful.json.objects.TelegramErrorJsonObject
import org.danceofvalkyries.job.data.telegram.jsonobjects.MessageData
import org.danceofvalkyries.utils.rest.Header
import org.danceofvalkyries.utils.rest.clients.http.HttpClient

class TelegramChatHttpClientDecorator(
    private val httpClient: HttpClient,
    private val gson: Gson,
) : HttpClient by httpClient {

    companion object {
        private val BAD_IMAGE_URL_REQUESTS = listOf(
            "Bad Request: wrong file identifier/HTTP URL specified",
            "Bad Request: failed to get HTTP URL content",
            "Bad Request: wrong remote file identifier specified: Wrong character in the string",
            "Bad Request: wrong remote file identifier specified: Wrong padding length",
        )
        const val BLUE_SCREEN = "https://neosmart.net/wiki/wp-content/uploads/sites/5/2013/08/unmountable-boot-volume.png"
    }

    override fun post(url: String, body: String, headers: List<Header>): HttpClient.Response {
        val response = httpClient.post(url, body, headers)
        if (response.responseCode == 400) {
            val errorJsonBody = gson.fromJson(response.responseBody, TelegramErrorJsonObject::class.java)
            if (BAD_IMAGE_URL_REQUESTS.contains(errorJsonBody.description)) {
                return httpClient.post(
                    url = url,
                    body = updateImage(body),
                    headers = headers
                )
            }
        }
        return response
    }

    private fun updateImage(requestBody: String): String {
        var messageRequest = gson.fromJson(requestBody, MessageData::class.java)
        messageRequest = messageRequest.copy(photo = BLUE_SCREEN)
        return gson.toJson(messageRequest)
    }
}