package org.danceofvalkyries.notion.impl

import notion.impl.client.NotionClientApi
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

class NotionApiImpl(
    private val clientApi: NotionClientApi
): NotionApi {

    override suspend fun getDataBase(id: NotionId): NotionDataBase {
        val response = clientApi.getNotionDb(id.get())
        return NotionDataBase(
            id = NotionId(response.id),
            name = response.name,
        )
    }

    override suspend fun update(flashCardNotionPage: FlashCardNotionPage) {
        val updateRequest = flashCardNotionPage.toUpdateKnowLevels()
        clientApi.updateInNotion(updateRequest)
    }

    override suspend fun getFlashCardPagesFromDb(notionId: NotionId): List<FlashCardNotionPage> {
        return clientApi.getContentFor(notionId.get())
            .map { it.toDomain() }
    }
}