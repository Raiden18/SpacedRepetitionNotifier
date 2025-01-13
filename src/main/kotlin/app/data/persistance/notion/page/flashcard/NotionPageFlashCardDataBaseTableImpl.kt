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
            id = flashCardPage.id.get(NotionId.Modifier.AS_IS),
            notionDbId = flashCardPage.notionDbID.get(NotionId.Modifier.AS_IS)
        )
        notionPageFlashCardDao.insert(entity)
    }

    override suspend fun getAllFor(notionDataBaseId: NotionId): List<FlashCardNotionPage> {
        return notionPageFlashCardDao.getAllFor(
            notionDataBaseId.get(NotionId.Modifier.AS_IS)
        ).map {
            FlashCardNotionPage(
                id = NotionId(it.id),
                coverUrl = it.imageUrl,
                notionDbID = NotionId(it.notionDbId),
                name = it.name,
                example = it.example,
                explanation = it.explanation,
                knowLevels = KnowLevels(emptyMap())
            )
        }
    }

    override suspend fun delete(flashCardPage: FlashCardNotionPage) {
        notionPageFlashCardDao.delete(flashCardPage.id.get(NotionId.Modifier.AS_IS))
    }

    override suspend fun clear() {
        notionPageFlashCardDao.clear()
    }
}