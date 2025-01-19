package org.danceofvalkyries.utils.rest.clients.http

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.danceofvalkyries.utils.rest.*
import org.danceofvalkyries.utils.rest.clients.http.HttpClient.Response

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
            responseBody = response.body?.string().orEmpty(),
            responseCode = response.code
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
            responseBody = response.body?.string().orEmpty(),
            responseCode = response.code
        )
    }

    override suspend fun patch(url: String, body: String, headers: List<Header>): Response {
        val applicationJson = ContentType(ContentTypes.ApplicationJson)
        val response = Request.Builder()
            .url(url)
            .headers(headers)
            .patch(body.toRequestBody(applicationJson.value.toMediaType()))
            .build()
            .request(okHttpClient)
        return Response(
            requestUrl = response.request.url.toString(),
            responseBody = response.body?.string().orEmpty(),
            responseCode = response.code
        )
    }
}