package org.danceofvalkyries.app.data.telegram.chat.restful

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.danceofvalkyries.utils.HttpClient
import org.danceofvalkyries.utils.HttpClient.Response
import org.danceofvalkyries.utils.rest.*

// TODO: Move to another package
class HttpClientImpl(
    private val okHttpClient: OkHttpClient,
) : HttpClient {

    override fun get(url: String, headers: List<Header>): Response {
        val response = Request.Builder()
            .url(url)
            .headers(headers)
            .get()
            .build()
            .request(okHttpClient)
        return Response(
            requestUrl = response.request.url.toString(),
            responseBody = response.body?.string().orEmpty()
        )
    }

    override fun post(url: String, body: String, headers: List<Header>): Response {
        val applicationJson = ContentType(ContentTypes.ApplicationJson)
        val response = Request.Builder()
            .url(url)
            .headers(headers)
            .post(body.toRequestBody(applicationJson.value.toMediaType()))
            .build()
            .request(okHttpClient)
        return Response(
            requestUrl = response.request.url.toString(),
            responseBody = response.body?.string().orEmpty()
        )
    }

    override fun patch(url: String, body: String, headers: List<Header>): Response {
        val applicationJson = ContentType(ContentTypes.ApplicationJson)
        val response = Request.Builder()
            .url(url)
            .headers(headers)
            .patch(body.toRequestBody(applicationJson.value.toMediaType()))
            .build()
            .request(okHttpClient)
        return Response(
            requestUrl = response.request.url.toString(),
            responseBody = response.body?.string().orEmpty()
        )
    }
}