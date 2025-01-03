package org.danceofvalkyries.notion.api

import com.google.gson.Gson
import notion.api.rest.response.FlashCardResponse
import notion.api.rest.response.NotionDbResponse
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.json.AuthorizationBearerHeader
import org.danceofvalkyries.json.ContentTypeApplicationJson
import org.danceofvalkyries.json.parse
import org.danceofvalkyries.json.toRequestBody
import org.danceofvalkyries.notion.api.rest.request.DbUrl
import org.danceofvalkyries.notion.api.rest.request.NotionApiVersionHeader
import org.danceofvalkyries.notion.api.rest.request.Query
import org.danceofvalkyries.notion.api.rest.request.SpacedRepetitionRequestBody

class NotionDataBaseApiImpl(
    private val gson: Gson,
    private val databaseId: String,
    private val client: OkHttpClient,
    private val apiVersion: String,
    private val apiKey: String,
) : NotionDataBaseApi {

    private val headers: Headers = listOf(
        AuthorizationBearerHeader(apiKey),
        NotionApiVersionHeader(apiVersion),
        ContentTypeApplicationJson(),
    ).let {
        val headersBuilder = Headers.Builder()
        it.forEach { headersBuilder.add(it.name, it.value) }
        headersBuilder.build()
    }

    override suspend fun getDescription(): NotionDbResponse {
        val url = DbUrl(databaseId)
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .get()
            .build()
        val response = client.newCall(request).execute()
        return response.parse<NotionDbResponse>(gson)
            .copy(id = databaseId)
    }

    override suspend fun getContent(): List<FlashCardResponse> {
        val url = Query(
            DbUrl(databaseId)
        )
        val body = SpacedRepetitionRequestBody(gson).toRequestBody()
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        return response.parse<FlashCardResponseWrapper>(gson).results
    }

    private data class FlashCardResponseWrapper(
        val results: List<FlashCardResponse>
    )
}
