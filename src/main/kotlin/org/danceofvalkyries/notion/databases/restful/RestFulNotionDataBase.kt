package org.danceofvalkyries.notion.databases.restful

import com.google.gson.Gson
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.notion.pages.restful.NotionApiVersionHeader
import org.danceofvalkyries.notion.pages.restful.RestfulNotionPageFlashCard
import org.danceofvalkyries.notion.pages.restful.jsonobjects.NotionDbResponse
import org.danceofvalkyries.notion.pages.restful.jsonobjects.RestFulNotionPage
import org.danceofvalkyries.notion.pages.restful.jsonobjects.SpacedRepetitionRequestBody
import org.danceofvalkyries.utils.rest.AuthorizationBearerHeader
import org.danceofvalkyries.utils.rest.ContentType
import org.danceofvalkyries.utils.rest.ContentTypes
import org.danceofvalkyries.utils.rest.clients.http.HttpClient
import org.danceofvalkyries.utils.rest.clients.http.parse

class RestFulNotionDataBase(
    override val id: String,
    private val apiKey: String,
    private val httpClient: HttpClient,
    private val gson: Gson,
) : NotionDataBase {

    private val headers = listOf(
        AuthorizationBearerHeader(apiKey),
        NotionApiVersionHeader("2022-06-28"),
        ContentType(ContentTypes.ApplicationJson)
    )

    override val name: String
        get() = httpClient.get(
            url = "https://api.notion.com/v1/databases/$id",
            headers = headers
        ).parse<NotionDbResponse>(gson).name


    override fun iterate(): Sequence<NotionPageFlashCard> {
        return httpClient.post(
            url = "https://api.notion.com/v1/databases/$id/query",
            headers = headers,
            body = SpacedRepetitionRequestBody(gson)
        ).parse<NotionPagesResponse>(gson)
            .results
            .asSequence()
            .map {
                RestfulNotionPageFlashCard(
                    id = it.id!!,
                    responseData = it,
                    httpClient = httpClient,
                    gson = gson,
                    headers = headers,
                )
            }
    }

    override fun getPageBy(pageId: String): NotionPageFlashCard {
        return RestfulNotionPageFlashCard(
            id = pageId,
            responseData = null,
            httpClient = httpClient,
            gson = gson,
            headers = headers,
        )
    }

    override fun clear() = error("Clearing pages from Notion is not going to be supported")
    override fun delete(pageId: String) = error("Deleting pages from Notion is not going to be supported")
    override fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard = error("Adding a page to Notion is not going to be supported")

    private data class NotionPagesResponse(
        val results: List<RestFulNotionPage>
    )
}