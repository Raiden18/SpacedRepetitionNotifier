package org.danceofvalkyries.app.data.repositories.flashcards

import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDbEntity
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.OnlineDictionary
import org.danceofvalkyries.notion.impl.restapi.models.NotionPageData

fun NotionPageData.toFlashCard(): FlashCard {
    val memorizedInfo = properties?.get("Name")?.title?.firstOrNull()?.text?.content.orEmpty()
    val example = properties?.get("Example")?.richText?.firstOrNull()?.text?.content.orEmpty().nullIfEmptyOrBlank()
    val answer = properties?.get("Explanation")?.richText?.firstOrNull()?.text?.content.orEmpty().nullIfEmptyOrBlank()
    return FlashCard(
        memorizedInfo = memorizedInfo,
        example = example,
        answer = answer,
        telegramImageUrl = cover?.external?.url?.nullIfEmptyOrBlank(),
        onlineDictionaries = emptyList(),
        metaInfo = FlashCard.MetaInfo(
            id = id.orEmpty(),
            notionDbId = parent?.databaseId.orEmpty(),
        )
    )
}

fun NotionPageFlashCardDbEntity.toFlashCard(onlineDictionaries: List<OnlineDictionary>): FlashCard {
    return FlashCard(
        memorizedInfo = name!!,
        example = example,
        answer = explanation,
        telegramImageUrl = imageUrl,
        onlineDictionaries = onlineDictionaries,
        metaInfo = FlashCard.MetaInfo(
            id = id,
            notionDbId = notionDbId!!
        )
    )
}

fun FlashCard.toEntity(): NotionPageFlashCardDbEntity {
    return NotionPageFlashCardDbEntity(
        name = memorizedInfo,
        example = example,
        explanation = answer,
        imageUrl = telegramImageUrl,
        id = metaInfo.id,
        notionDbId = metaInfo.notionDbId,
    )
}

private fun String.nullIfEmptyOrBlank(): String? {
    if (isEmpty()) {
        return null
    }
    if (isBlank()) {
        return null
    }
    return this
}