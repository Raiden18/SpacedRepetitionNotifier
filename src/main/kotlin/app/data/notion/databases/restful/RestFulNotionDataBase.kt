package org.danceofvalkyries.app.data.notion.databases.restful

import com.google.gson.Gson
import notion.impl.client.models.RestFulNotionPage
import notion.impl.client.models.request.NotionApiVersionHeader
import notion.impl.client.models.request.SpacedRepetitionRequestBody
import notion.impl.client.models.response.NotionDbResponse
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.notion.pages.restful.RestfulNotionPageFlashCard
import org.danceofvalkyries.utils.HttpClient
import org.danceofvalkyries.utils.parse
import org.danceofvalkyries.utils.rest.AuthorizationBearerHeader
import org.danceofvalkyries.utils.rest.ContentType
import org.danceofvalkyries.utils.rest.ContentTypes

class RestFulNotionDataBase(
    override val id: String,
    private val apiKey: String,
    private val httpClient: HttpClient,
    private val gson: Gson,
    private val okHttpClient: OkHttpClient,
) : NotionDataBase {

    override val name: String
        get() = httpClient.get(
            url = "https://api.notion.com/v1/databases/$id",
            headers = listOf(
                AuthorizationBearerHeader(apiKey),
                NotionApiVersionHeader("2022-06-28"),
                ContentType(ContentTypes.ApplicationJson)
            )
        ).parse<NotionDbResponse>(gson).name


    override fun iterate(): Sequence<NotionPageFlashCard> {
        return httpClient.post(
            url = "https://api.notion.com/v1/databases/$id/query",
            headers = listOf(
                AuthorizationBearerHeader(apiKey),
                NotionApiVersionHeader("2022-06-28"),
                ContentType(ContentTypes.ApplicationJson)
            ),
            body = SpacedRepetitionRequestBody(gson)
        ).parse<NotionPagesResponse>(gson)
            .results
            .asSequence()
            .map {
                RestfulNotionPageFlashCard(
                    id = it.id!!,
                    apiKey = apiKey,
                    responseData = it,
                    client = okHttpClient,
                    gson = gson,
                )
            }
    }

    override fun add(
        id: String,
        coverUrl: String?,
        name: String,
        example: String?,
        explanation: String?,
        knowLevels: Map<Int, Boolean>
    ): NotionPageFlashCard = error("Adding a page to Notion is not supported")

    override fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard =
        error("Adding a page to Notion is not supported")

    override fun getPageBy(pageId: String): NotionPageFlashCard {
        return RestfulNotionPageFlashCard(
            apiKey = apiKey,
            responseData = null,
            client = okHttpClient,
            gson = gson,
            id = pageId
        )
    }

    override fun clear() = error("Clearing pages from Notion is not supported")

    override fun delete(pageId: String) = error("Deleting pages from Notion is not supported")

    private data class NotionPagesResponse(
        val results: List<RestFulNotionPage>
    )
}