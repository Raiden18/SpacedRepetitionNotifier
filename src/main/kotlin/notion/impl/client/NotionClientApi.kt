package notion.impl.client

import notion.impl.client.models.response.NotionDbResponse
import notion.impl.client.models.NotionPageData

interface NotionClientApi {
    suspend fun getNotionDb(id: String): NotionDbResponse
    suspend fun getContentFor(id: String): List<NotionPageData>
    suspend fun updateInNotion(notionPageData: NotionPageData)
}