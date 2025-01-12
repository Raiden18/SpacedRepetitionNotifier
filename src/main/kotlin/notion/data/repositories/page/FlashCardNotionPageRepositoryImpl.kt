package org.danceofvalkyries.notion.data.repositories.page

import org.danceofvalkyries.notion.data.repositories.api.NotionApi
import org.danceofvalkyries.notion.data.repositories.api.toDomain
import org.danceofvalkyries.notion.data.repositories.api.toUpdateKnowLevels
import org.danceofvalkyries.notion.domain.models.FlashCardNotionPage
import org.danceofvalkyries.notion.domain.repositories.FlashCardNotionPageRepository

class FlashCardNotionPageRepositoryImpl(
    private val notionApi: NotionApi,
) : FlashCardNotionPageRepository {

    override suspend fun getFromNotion(id: String): FlashCardNotionPage {
        val response = notionApi.getNotionPage(id)
        return response.toDomain()
    }

    override suspend fun updateInNotion(flashCardNotionPage: FlashCardNotionPage) {
        val updateRequest = flashCardNotionPage.toUpdateKnowLevels()
        notionApi.updateInNotion(updateRequest)
    }

    override suspend fun getAllFromDb(id: String): List<FlashCardNotionPage> {
        return notionApi.getContentFor(id)
            .map { it.toDomain() }
    }
}