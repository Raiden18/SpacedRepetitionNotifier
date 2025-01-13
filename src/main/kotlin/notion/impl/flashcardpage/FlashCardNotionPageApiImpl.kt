package org.danceofvalkyries.notion.impl.flashcardpage

import notion.impl.client.NotionApi
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.notion.impl.toDomain
import org.danceofvalkyries.notion.impl.toUpdateKnowLevels

class FlashCardNotionPageApiImpl(
    private val notionApi: NotionApi,
) : FlashCardNotionPageApi {

    override suspend fun getFromNotion(notionId: NotionId): FlashCardNotionPage {
        val response = notionApi.getNotionPage(notionId.get(NotionId.Modifier.URL_FRIENDLY))
        return response.toDomain()
    }

    override suspend fun updateInNotion(flashCardNotionPage: FlashCardNotionPage) {
        val updateRequest = flashCardNotionPage.toUpdateKnowLevels()
        notionApi.updateInNotion(updateRequest)
    }

    override suspend fun getAllFromDb(notionId: NotionId): List<FlashCardNotionPage> {
        return notionApi.getContentFor(notionId.get(NotionId.Modifier.URL_FRIENDLY))
            .map { it.toDomain() }
    }
}