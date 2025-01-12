package org.danceofvalkyries.app.data.repositories.flashcards

import org.danceofvalkyries.app.data.repositories.flashcards.db.FlashCardDbEntity
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.app.domain.models.ImageUrl
import org.danceofvalkyries.app.domain.models.OnlineDictionary
import org.danceofvalkyries.app.domain.models.text.Text
import org.danceofvalkyries.notion.data.repositories.api.models.NotionPageData

fun NotionPageData.toFlashCard(): FlashCard {
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
            notionDbId = Id(parent?.databaseId.orEmpty()),
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
            notionDbId = Id(notionDbId!!)
        )
    )
}

fun FlashCard.toEntity(): FlashCardDbEntity {
    return FlashCardDbEntity(
        memorizedInfo = memorizedInfo.getValue(Text.TextModifier.AS_IS),
        example = example?.getValue(Text.TextModifier.AS_IS),
        answer = answer?.getValue(Text.TextModifier.AS_IS),
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