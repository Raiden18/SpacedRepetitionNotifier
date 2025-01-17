package org.danceofvalkyries.app.data.telegram.users.user

import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.telegram.users.HumanUser
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

class TelegramHumanUserImpl(
    private val localDbNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
) : HumanUser {

    override suspend fun forget(flashCardId: String) {
        val flashCard = localDbNotionDataBases
            .iterate()
            .flatMap { it.iterate() }
            .map {
                FlashCardNotionPage(
                    name = it.name,
                    coverUrl = it.coverUrl,
                    notionDbID = NotionId(it.notionDbID),
                    id = NotionId(it.id),
                    example = it.example,
                    explanation = it.explanation,
                    knowLevels = KnowLevels(it.knowLevels)
                )
            }.first { it.id.rawValue == flashCardId }
        val forgottenFlashCard = flashCard.forget()
        localDbNotionDataBases.iterate().forEach { it.delete(forgottenFlashCard.id.rawValue) }
        val restfullDataBase = restfulNotionDataBases.getBy(flashCard.notionDbID.rawValue)
        restfullDataBase.getPageBy(flashCardId).setKnowLevels(forgottenFlashCard.knowLevels.levels)
    }

    override suspend fun recall(flashCardId: String) {

    }
}