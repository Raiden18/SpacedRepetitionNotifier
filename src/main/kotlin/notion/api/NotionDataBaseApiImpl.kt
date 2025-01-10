package org.danceofvalkyries.notion.api

import com.google.gson.Gson
import notion.api.rest.response.FlashCardResponse
import notion.api.rest.response.NotionDbResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.json.*
import org.danceofvalkyries.notion.api.rest.DatabaseUrl
import org.danceofvalkyries.notion.api.rest.request.NotionApiVersionHeader
import org.danceofvalkyries.notion.api.rest.request.SpacedRepetitionRequestBody
import org.danceofvalkyries.notion.api.rest.response.NotionPageResponse

class NotionDataBaseApiImpl(
    private val gson: Gson,
    private val databaseId: String,
    private val client: OkHttpClient,
    private val apiKey: String,
) : NotionDataBaseApi {

    private val headers = listOf(
        AuthorizationBearerHeader(apiKey),
        NotionApiVersionHeader("2022-06-28"),
        ContentType(ContentTypes.ApplicationJson),
    )

    private val urls: DatabaseUrl
        get() = DatabaseUrl(databaseId)

    override suspend fun getDescription(): NotionDbResponse {
        return Request.Builder()
            .url(urls.dataBases())
            .headers(headers)
            .get()
            .build()
            .request(client)
            .parse<NotionDbResponse>(gson)
            .copy(id = databaseId)
    }

    override suspend fun getContent(): List<NotionPageResponse> {
        return Request.Builder()
            .url(urls.databasesQuery())
            .headers(headers)
            .post(SpacedRepetitionRequestBody(gson))
            .build()
            .request(client)
            .parse<FlashCardResponseWrapper>(gson)
            .results
    }

    private data class FlashCardResponseWrapper(
        val results: List<NotionPageResponse>
    )
}
