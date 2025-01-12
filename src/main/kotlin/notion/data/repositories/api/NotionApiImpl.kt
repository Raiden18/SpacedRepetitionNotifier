package org.danceofvalkyries.notion.data.repositories.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.json.*
import org.danceofvalkyries.notion.data.repositories.api.rest.request.SpacedRepetitionRequestBody
import org.danceofvalkyries.notion.data.repositories.api.rest.DatabaseUrl
import org.danceofvalkyries.notion.data.repositories.api.rest.request.NotionApiVersionHeader
import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionDbResponse
import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionPageResponse
import org.danceofvalkyries.utils.rest.headers
import org.danceofvalkyries.utils.rest.parse
import org.danceofvalkyries.utils.rest.post
import org.danceofvalkyries.utils.rest.request

class NotionApiImpl(
    private val gson: Gson,
    private val client: OkHttpClient,
    private val apiKey: String,
) : NotionApi {

    private val headers = listOf(
        AuthorizationBearerHeader(apiKey),
        NotionApiVersionHeader("2022-06-28"),
        ContentType(ContentTypes.ApplicationJson),
    )

    private val urls = DatabaseUrl()

    override suspend fun getNotionDb(id: String): NotionDbResponse {
        return Request.Builder()
            .url(urls.dataBases(id))
            .headers(headers)
            .get()
            .build()
            .request(client)
            .parse<NotionDbResponse>(gson)
            .copy(id = id)
    }

    override suspend fun getContentFor(id: String): List<NotionPageResponse> {
        return Request.Builder()
            .url(urls.databasesQuery(id))
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
