package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs

import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.notion.api.NotionApi
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

//TODO: Add unit tests
class SpaceRepetitionSessionImpl(
    private val notionDataBases: NotionDataBases,
    private val notionFlashCardPage: NotionApi,
) : SpaceRepetitionSession {

    override suspend fun getCurrentFlashCard(flashCardId: NotionId): FlashCardNotionPage {
        return notionDataBases.iterate()
            .flatMap { it.iterate() }
            .map {
                FlashCardNotionPage(
                    name = it.name,
                    coverUrl = it.coverUrl,
                    notionDbID = NotionId(it.notionDbID),
                    id = NotionId(it.id),
                    example = it.example,
                    explanation = it.explanation,
                    knowLevels = KnowLevels(it.knowLevels.levels)
                )
            }
            .first { it.id.rawValue == flashCardId.rawValue }
    }

    override suspend fun getNextFlashCard(databaseId: NotionId): FlashCardNotionPage? {
        return notionDataBases.iterate()
            .flatMap { it.iterate() }
            .map {
                FlashCardNotionPage(
                    name = it.name,
                    coverUrl = it.coverUrl,
                    notionDbID = NotionId(it.notionDbID),
                    id = NotionId(it.id),
                    example = it.example,
                    explanation = it.explanation,
                    knowLevels = KnowLevels(it.knowLevels.levels)
                )
            }
            .firstOrNull { it.notionDbID.rawValue == databaseId.rawValue }
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
                    knowLevels = KnowLevels(it.knowLevels.levels)
                )
            }
            .filter { it.id.rawValue == flashCardId.rawValue }
            .first()
        val recalledFlashCard = flashCard.recall()
        notionDataBases.iterate().forEach {
            it.delete(recalledFlashCard.id.rawValue)
        }
        notionFlashCardPage.update(recalledFlashCard)
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
                    knowLevels = KnowLevels(it.knowLevels.levels)
                )
            }
            .first { it.id.rawValue == flashCardId.rawValue }
        val forgottenFlashCard = flashCard.forget()
        notionDataBases.iterate().forEach {
            it.delete(forgottenFlashCard.id.rawValue)
        }
        notionFlashCardPage.update(forgottenFlashCard)
    }
}