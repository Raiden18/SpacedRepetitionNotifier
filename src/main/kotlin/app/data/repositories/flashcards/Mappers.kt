package org.danceofvalkyries.app.data.repositories.flashcards

import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.FlashCardDbEntity
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

fun FlashCardDbEntity.toFlashCard(onlineDictionaries: List<OnlineDictionary>): FlashCard {
    return FlashCard(
        memorizedInfo = memorizedInfo!!,
        example = example,
        answer = answer,
        telegramImageUrl = imageUrl,
        onlineDictionaries = onlineDictionaries,
        metaInfo = FlashCard.MetaInfo(
            id = cardId,
            notionDbId = notionDbId!!
        )
    )
}

fun FlashCard.toEntity(): FlashCardDbEntity {
    return FlashCardDbEntity(
        memorizedInfo = memorizedInfo,
        example = example,
        answer = answer,
        imageUrl = telegramImageUrl,
        cardId = metaInfo.id,
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