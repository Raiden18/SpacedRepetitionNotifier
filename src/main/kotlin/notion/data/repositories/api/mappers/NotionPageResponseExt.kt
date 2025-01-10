package org.danceofvalkyries.notion.data.repositories.api.mappers

import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionPageResponse
import org.danceofvalkyries.notion.domain.models.FlashCard

fun NotionPageResponse.toFlashCard(): FlashCard {
    return FlashCard(
        memorizedInfo = properties?.get("Name")?.title?.firstOrNull()?.text?.content.orEmpty(),
        example = properties?.get("Example")?.richText?.firstOrNull()?.text?.content.orEmpty(),
        answer = properties?.get("Explanation")?.richText?.firstOrNull()?.text?.content.orEmpty(),
    )
}