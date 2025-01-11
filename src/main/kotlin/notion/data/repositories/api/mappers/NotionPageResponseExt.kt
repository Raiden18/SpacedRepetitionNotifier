package org.danceofvalkyries.notion.data.repositories.api.mappers

import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionPageResponse
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.ImageUrl
import org.danceofvalkyries.notion.domain.models.NotionDbId

fun NotionPageResponse.toFlashCard(): FlashCard {
    val memorizedInfo = properties?.get("Name")?.title?.firstOrNull()?.text?.content.orEmpty()
    val example = properties?.get("Example")?.richText?.firstOrNull()?.text?.content.orEmpty().nullIfEmptyOrBlank()
    val answer = properties?.get("Explanation")?.richText?.firstOrNull()?.text?.content.orEmpty().nullIfEmptyOrBlank()
    return FlashCard(
        memorizedInfo = memorizedInfo,
        example = example,
        answer = answer,
        imageUrl = cover?.external?.url?.nullIfEmptyOrBlank()?.let(::ImageUrl),
        metaInfo = FlashCard.MetaInfo(
            id = id.orEmpty(),
            notionDbId = NotionDbId(parent?.databaseId.orEmpty()),
        )
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
