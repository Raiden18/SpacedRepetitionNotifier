package notion.impl.client

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.danceofvalkyries.utils.rest.AuthorizationBearerHeader
import org.danceofvalkyries.utils.rest.ContentType
import org.danceofvalkyries.utils.rest.ContentTypes
import notion.impl.client.models.NotionPageData
import notion.impl.client.models.request.NotionApiVersionHeader
import notion.impl.client.models.request.SpacedRepetitionRequestBody
import notion.impl.client.models.response.NotionDbResponse
import org.danceofvalkyries.utils.rest.*

class NotionClientApiImpl(
    private val gson: Gson,
    private val client: OkHttpClient,
    private val apiKey: String,
) : NotionClientApi {

    private val headers = listOf(
        AuthorizationBearerHeader(apiKey),
        NotionApiVersionHeader("2022-06-28"),
        ContentType(ContentTypes.ApplicationJson),
    )

    private val urls = NotionUrls()

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

    override suspend fun getContentFor(id: String): List<NotionPageData> {
        return Request.Builder()
            .url(urls.databasesQuery(id))
            .headers(headers)
            .post(SpacedRepetitionRequestBody(gson))
            .build()
            .request(client)
            .parse<NotionPagesResponse>(gson)
            .results
    }

    override suspend fun getNotionPage(id: String): NotionPageData {
        return Request.Builder()
            .url(urls.pages(id))
            .get()
            .headers(headers)
            .build()
            .request(client)
            .parse(gson)
    }

    override suspend fun updateInNotion(notionPageData: NotionPageData) {
        Request.Builder()
            .headers(headers)
            .url(urls.pages(notionPageData.id!!))
            .patch(gson.toJson(notionPageData))
            .build()
            .request(client)
    }

    private data class NotionPagesResponse(
        val results: List<NotionPageData>
    )
}
