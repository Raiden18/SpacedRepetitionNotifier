package org.danceofvalkyries.app.data.restful.notion.databases

import app.domain.notion.databases.NotionDataBase
import com.google.gson.Gson
import notion.impl.client.models.RestFulNotionPage
import notion.impl.client.models.request.NotionApiVersionHeader
import notion.impl.client.models.request.SpacedRepetitionRequestBody
import notion.impl.client.models.response.NotionDbResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.app.data.restful.notion.page.RestfulNotionPageFlashCard
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard
import org.danceofvalkyries.utils.rest.*

class RestFulNotionDataBase(
    override val id: String,
    private val apiKey: String,
    private val client: OkHttpClient,
    private val gson: Gson,
) : NotionDataBase {

    override val name: String
        get() = Request.Builder()
            .url("https://api.notion.com/v1/databases/$id")
            .headers(
                listOf(
                    AuthorizationBearerHeader(apiKey),
                    NotionApiVersionHeader("2022-06-28"),
                    ContentType(ContentTypes.ApplicationJson),
                )
            )
            .get()
            .build()
            .request(client)
            .parse<NotionDbResponse>(gson)
            .copy(id = id)
            .name

    override fun iterate(): Sequence<NotionPageFlashCard> {
        return Request.Builder()
            .url("https://api.notion.com/v1/databases/$id/query")
            .headers(
                listOf(
                    AuthorizationBearerHeader(apiKey),
                    NotionApiVersionHeader("2022-06-28"),
                    ContentType(ContentTypes.ApplicationJson),
                )
            )
            .post(SpacedRepetitionRequestBody(gson))
            .build()
            .request(client)
            .parse<NotionPagesResponse>(gson)
            .results
            .asSequence()
            .map {
                RestfulNotionPageFlashCard(
                    id = it.id!!,
                    apiKey = apiKey,
                    responseData = it,
                    client = client,
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
            client = client,
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