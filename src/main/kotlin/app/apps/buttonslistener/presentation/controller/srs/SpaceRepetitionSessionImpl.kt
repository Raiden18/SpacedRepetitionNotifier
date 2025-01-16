package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs

import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

class SpaceRepetitionSessionImpl(
    private val notionDataBases: NotionDataBases,
    private val restfullNotionDataBase: NotionDataBases,
) : SpaceRepetitionSession {

    override suspend fun getCurrentFlashCard(flashCardId: NotionId): NotionPageFlashCard {
        return notionDataBases.iterate()
            .flatMap { it.iterate() }
            .first { it.id == flashCardId.rawValue }
    }

    override suspend fun getNextFlashCard(databaseId: NotionId): NotionPageFlashCard? {
        return notionDataBases.iterate()
            .flatMap { it.iterate() }
            .firstOrNull { it.notionDbID == databaseId.rawValue }
    }

    override suspend fun recall(flashCardId: NotionId) {
        val flashCard = notionDataBases.iterate()
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
            }
            .filter { it.id.rawValue == flashCardId.rawValue }
            .first()
        val recalledFlashCard = flashCard.recall()
        notionDataBases.iterate().forEach {
            it.delete(recalledFlashCard.id.rawValue)
        }
        val restfullDataBase = restfullNotionDataBase.getBy(flashCard.notionDbID.rawValue)
        restfullDataBase.getPageBy(flashCardId.get()).setKnowLevels(recalledFlashCard.knowLevels.levels)
    }

    override suspend fun forget(flashCardId: NotionId) {
        val flashCard = notionDataBases
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
            }.first { it.id.rawValue == flashCardId.rawValue }
        val forgottenFlashCard = flashCard.forget()
        notionDataBases.iterate().forEach { it.delete(forgottenFlashCard.id.rawValue) }
        val restfullDataBase = restfullNotionDataBase.getBy(flashCard.notionDbID.rawValue)
        restfullDataBase.getPageBy(flashCardId.get()).setKnowLevels(forgottenFlashCard.knowLevels.levels)
    }
}