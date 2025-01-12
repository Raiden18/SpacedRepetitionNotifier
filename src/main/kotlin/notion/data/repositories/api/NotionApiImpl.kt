package org.danceofvalkyries.notion.data.repositories.api

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.danceofvalkyries.json.*
import org.danceofvalkyries.notion.data.repositories.api.rest.request.SpacedRepetitionRequestBody
import org.danceofvalkyries.notion.data.repositories.api.rest.DatabaseUrl
import org.danceofvalkyries.notion.data.repositories.api.rest.request.NotionApiVersionHeader
import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionDbResponse
import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionPageResponse
import org.danceofvalkyries.utils.rest.*

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
            .parse<NotionPagesResponse>(gson)
            .results
    }

    override suspend fun recall() {
        val notionPage = Request.Builder()
            .url("https://api.notion.com/v1/pages/17240270a14380d8a80bc055a9267cf6")
            .headers(headers)
            .get()
            .build()
            .request(client)
            .parse<NotionPageResponse>(gson)


        Request.Builder()
            .url("https://api.notion.com/v1/pages/17240270a14380d8a80bc055a9267cf6")
            .headers(headers)
            .patch(
                jsonObject {
                    "properties" to jsonObject {
                        "Know Level 4" to jsonObject {
                            "checkbox" to true
                        }
                    }
                }.let { gson.toJson(it) }.toRequestBody(ContentType(ContentTypes.ApplicationJson).value.toMediaType())
            ).build()
            .request(client)
            .parse<NotionPageResponse>(gson)
    }

    override suspend fun forgot() {
        TODO("Not yet implemented")
    }

    private data class NotionPagesResponse(
        val results: List<NotionPageResponse>
    )
}
