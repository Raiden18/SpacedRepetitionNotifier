package org.danceofvalkyries.app.data.notion.pages.restful

import com.google.gson.Gson
import notion.impl.client.models.PropertyData
import notion.impl.client.models.RestFulNotionPage
import notion.impl.client.models.request.NotionApiVersionHeader
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.utils.rest.clients.http.HttpClient
import org.danceofvalkyries.utils.rest.AuthorizationBearerHeader
import org.danceofvalkyries.utils.rest.ContentType
import org.danceofvalkyries.utils.rest.ContentTypes

class RestfulNotionPageFlashCard(
    private val apiKey: String,
    override val id: String,
    private val responseData: RestFulNotionPage? = null,
    private val httpClient: HttpClient,
    private val gson: Gson,
) : NotionPageFlashCard {

    override val coverUrl: String?
        get() = responseData!!.coverUrl

    override val notionDbID: String
        get() = responseData!!.notionDbID

    override val name: String
        get() = responseData!!.name

    override val example: String?
        get() = responseData!!.example

    override val explanation: String?
        get() = responseData!!.explanation

    override val knowLevels: Map<Int, Boolean>
        get() = responseData!!.knowLevels

    override fun setKnowLevels(knowLevels: Map<Int, Boolean>) {
        val updatedNotionPage = RestFulNotionPage(
            id = null,
            properties = knowLevels
                .filterValues { it != null }
                .mapKeys { "Know Level ${it.key}" }
                .mapValues { PropertyData(checkbox = it.value) }
        )
        httpClient.patch(
            url = "https://api.notion.com/v1/pages/$id",
            headers = listOf(
                AuthorizationBearerHeader(apiKey),
                NotionApiVersionHeader("2022-06-28"),
                ContentType(ContentTypes.ApplicationJson),
            ),
            body = gson.toJson(updatedNotionPage)
        )
    }
}