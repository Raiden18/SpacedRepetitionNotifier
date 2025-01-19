package org.danceofvalkyries.notion.pages.restful

import com.google.gson.Gson
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.notion.pages.restful.jsonobjects.PropertyData
import org.danceofvalkyries.notion.pages.restful.jsonobjects.RestFulNotionPage
import org.danceofvalkyries.utils.rest.Header
import org.danceofvalkyries.utils.rest.clients.http.HttpClient

class RestfulNotionPageFlashCard(
    override val id: String,
    private val responseData: RestFulNotionPage? = null,
    private val httpClient: HttpClient,
    private val gson: Gson,
    private val headers: List<Header>
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

    override suspend fun setKnowLevels(knowLevels: Map<Int, Boolean>) {
        val updatedNotionPage = RestFulNotionPage(
            id = null,
            properties = knowLevels
                .filterValues { it != null }
                .mapKeys { "Know Level ${it.key}" }
                .mapValues { PropertyData(checkbox = it.value) }
        )
        httpClient.patch(
            url = "https://api.notion.com/v1/pages/$id",
            headers = headers,
            body = gson.toJson(updatedNotionPage)
        )
    }
}