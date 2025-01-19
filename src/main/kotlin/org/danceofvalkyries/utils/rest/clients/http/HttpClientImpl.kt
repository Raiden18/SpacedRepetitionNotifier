package org.danceofvalkyries.utils.rest.clients.http

import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.danceofvalkyries.utils.rest.ContentType
import org.danceofvalkyries.utils.rest.ContentTypes
import org.danceofvalkyries.utils.rest.Header
import org.danceofvalkyries.utils.rest.clients.http.HttpClient.Response
import kotlin.coroutines.suspendCoroutine

class HttpClientImpl(
    private val okHttpClient: OkHttpClient,
) : HttpClient {

    override suspend fun get(url: String, headers: List<Header>): Response {
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

    override suspend fun post(url: String, body: String, headers: List<Header>): Response {
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

    private fun Request.Builder.headers(headers: List<Header>): Request.Builder {
        val headersBuilder = Headers.Builder()
        headers.forEach { headersBuilder.add(it.name, it.value) }
        return headers(headersBuilder.build())
    }

    private suspend fun Request.request(client: OkHttpClient): okhttp3.Response {
        return suspendCoroutine { continuation ->
            client.newCall(this).enqueue(OkHttpCallbackCoroutinesAdapter(continuation))
        }
    }
}