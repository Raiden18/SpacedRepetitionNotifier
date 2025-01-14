package org.danceofvalkyries.app.data.persistance.notion.page.flashcard

import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDao
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDbEntity
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

class NotionPageFlashCardDataBaseTableImpl(
    private val notionPageFlashCardDao: NotionPageFlashCardDao,
) : NotionPageFlashCardDataBaseTable {

    override suspend fun insert(flashCardPage: FlashCardNotionPage) {
        val entity = NotionPageFlashCardDbEntity(
            name = flashCardPage.name,
            example = flashCardPage.example,
            explanation = flashCardPage.explanation,
            imageUrl = flashCardPage.coverUrl,
            id = flashCardPage.id.rawValue,
            notionDbId = flashCardPage.notionDbID.get(),
            knowLevels = flashCardPage.knowLevels.levels
        )
        notionPageFlashCardDao.insert(entity)
    }

    override suspend fun insert(flashCardPages: List<FlashCardNotionPage>) {
        flashCardPages.forEach { insert(it) }
    }

    override suspend fun getAllFor(notionDataBaseId: NotionId): List<FlashCardNotionPage> {
        return notionPageFlashCardDao.getAllFor(notionDataBaseId.get()).map {
            FlashCardNotionPage(
                id = NotionId(it.id),
                coverUrl = it.imageUrl,
                notionDbID = NotionId(it.notionDbId),
                name = it.name,
                example = it.example,
                explanation = it.explanation,
                knowLevels = KnowLevels(
                    it.knowLevels
                        .filterValues { it != null }
                        .mapValues { it.value!! }
                )
            )
        }
    }

    override suspend fun delete(flashCardPage: FlashCardNotionPage) {
        notionPageFlashCardDao.delete(flashCardPage.id.rawValue)
    }

    override suspend fun getFirstFor(notionDataBaseId: NotionId): FlashCardNotionPage? {
        return getAllFor(notionDataBaseId).firstOrNull()
    }

    override suspend fun getBy(id: NotionId): FlashCardNotionPage? {
        val response = notionPageFlashCardDao.getBy(id.rawValue) ?: return null
        return FlashCardNotionPage(
            id = NotionId(response.id),
            coverUrl = response.imageUrl,
            notionDbID = NotionId(response.notionDbId),
            name = response.name,
            example = response.example,
            explanation = response.explanation,
            knowLevels = KnowLevels(
                response.knowLevels
                    .filterValues { it != null }
                    .mapValues { it.value!! }
            )
        )
    }

    override suspend fun clear() {
        notionPageFlashCardDao.clear()
    }
}