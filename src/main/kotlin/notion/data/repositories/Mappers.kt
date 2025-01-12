package org.danceofvalkyries.notion.data.repositories

import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionPageResponse
import org.danceofvalkyries.notion.data.repositories.db.flashcards.FlashCardDbEntity
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.ImageUrl
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.notion.domain.models.OnlineDictionary

fun NotionPageResponse.toFlashCard(): FlashCard {
    val memorizedInfo = properties?.get("Name")?.title?.firstOrNull()?.text?.content.orEmpty()
    val example = properties?.get("Example")?.richText?.firstOrNull()?.text?.content.orEmpty().nullIfEmptyOrBlank()
    val answer = properties?.get("Explanation")?.richText?.firstOrNull()?.text?.content.orEmpty().nullIfEmptyOrBlank()
    return FlashCard(
        memorizedInfo = memorizedInfo,
        example = example,
        answer = answer,
        imageUrl = cover?.external?.url?.nullIfEmptyOrBlank()?.let(::ImageUrl),
        onlineDictionaries = emptyList(),
        metaInfo = FlashCard.MetaInfo(
            id = id.orEmpty(),
            notionDbId = NotionDbId(parent?.databaseId.orEmpty()),
        )
    )
}

fun FlashCardDbEntity.toFlashCard(onlineDictionaries: List<OnlineDictionary>): FlashCard {
    return FlashCard(
        memorizedInfo = memorizedInfo!!,
        example = example,
        answer = answer,
        imageUrl = imageUrl?.let(::ImageUrl),
        onlineDictionaries = onlineDictionaries,
        metaInfo = FlashCard.MetaInfo(
            id = cardId,
            notionDbId = NotionDbId(notionDbId!!)
        )
    )
}

fun FlashCard.toEntity(): FlashCardDbEntity {
    return FlashCardDbEntity(
        memorizedInfo = memorizedInfo.getValue(),
        example = example?.getValue(),
        answer = answer?.getValue(),
        imageUrl = imageUrl?.url,
        cardId = metaInfo.id,
        notionDbId = metaInfo.notionDbId.valueId,
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
