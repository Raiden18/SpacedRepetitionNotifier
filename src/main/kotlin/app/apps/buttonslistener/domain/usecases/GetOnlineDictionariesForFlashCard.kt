package org.danceofvalkyries.app.apps.buttonslistener.domain.usecases

import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard
import org.danceofvalkyries.config.domain.ObservedDatabase
import org.danceofvalkyries.dictionary.api.OnlineDictionary
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage

fun interface GetOnlineDictionariesForFlashCard {
    suspend fun execute(flashCardNotionPage: NotionPageFlashCard): List<OnlineDictionary>
}

fun GetOnlineDictionariesForFlashCard(
    observedDatabases: List<ObservedDatabase>
): GetOnlineDictionariesForFlashCard {
    return GetOnlineDictionariesForFlashCard {
        observedDatabases
            .map { it.id to it.dictionaries }
            .toMap()[it.notionDbID]
            ?.map { OnlineDictionary(it) }
            ?: emptyList()

    }
}