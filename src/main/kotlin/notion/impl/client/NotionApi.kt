package notion.impl.client

import notion.impl.client.models.response.NotionDbResponse
import notion.impl.client.models.NotionPageData

interface NotionApi {
    suspend fun getNotionDb(id: String): NotionDbResponse
    suspend fun getContentFor(id: String): List<NotionPageData>
    suspend fun getNotionPage(id: String): NotionPageData
    suspend fun updateInNotion(notionPageData: NotionPageData)
}