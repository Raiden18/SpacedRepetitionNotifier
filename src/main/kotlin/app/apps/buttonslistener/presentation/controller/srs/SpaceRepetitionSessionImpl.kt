package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.srs

import app.domain.notion.databases.NotionDataBases
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.data.restful.notion.page.RestfulNotionPageFlashCard
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

//TODO: Add unit tests
class SpaceRepetitionSessionImpl(
    private val notionDataBases: NotionDataBases,
    private val apiKey: String,
    private val gson: Gson,
    private val client: OkHttpClient,
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
                    knowLevels = KnowLevels(it.knowLevels.levels)
                )
            }
            .filter { it.id.rawValue == flashCardId.rawValue }
            .first()
        val recalledFlashCard = flashCard.recall()
        notionDataBases.iterate().forEach {
            it.delete(recalledFlashCard.id.rawValue)
        }
        RestfulNotionPageFlashCard(
            apiKey = apiKey,
            responseData = null,
            client = client,
            gson = gson,
            id = flashCardId.get()
        ).setKnowLevels(
            object : NotionPageFlashCard.KnowLevels {
                override val levels: Map<Int, Boolean> = recalledFlashCard.knowLevels.levels
            }
        )

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
        RestfulNotionPageFlashCard(
            apiKey = apiKey,
            responseData = null,
            client = client,
            gson = gson,
            id = flashCardId.get()
        ).setKnowLevels(
            object : NotionPageFlashCard.KnowLevels {
                override val levels: Map<Int, Boolean> = forgottenFlashCard.knowLevels.levels
            }
        )
    }
}