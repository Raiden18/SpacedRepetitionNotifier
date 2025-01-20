package org.danceofvalkyries.notion.pages.restful

import com.google.gson.Gson
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.notion.pages.restful.jsonobjects.PropertyData
import org.danceofvalkyries.notion.pages.restful.jsonobjects.RestFulNotionPage
import org.danceofvalkyries.utils.rest.Header
import org.danceofvalkyries.utils.rest.clients.http.HttpClient

class RestfulNotionPageFlashCard(
    private val id: String,
    private val responseData: RestFulNotionPage? = null,
    private val httpClient: HttpClient,
    private val gson: Gson,
    private val headers: List<Header>
) : NotionPageFlashCard {

    override suspend fun getId(): String {
        return id
    }

    override suspend fun getCoverUrl(): String? {
        return responseData!!.coverUrl
    }

    override suspend fun getNotionDbId(): String {
        return responseData!!.notionDbID
    }

    override suspend fun getName(): String {
        return responseData!!.name
    }

    override suspend fun getExample(): String? {
        return responseData!!.example
    }

    override suspend fun getExplanation(): String? {
        return responseData!!.explanation
    }

    override suspend fun getKnowLevels(): Map<Int, Boolean> {
        return responseData!!.knowLevels
    }

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