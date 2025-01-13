package org.danceofvalkyries.notion.impl

import org.danceofvalkyries.notion.impl.restapi.models.NotionPageData
import org.danceofvalkyries.notion.impl.restapi.models.PropertyData
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

fun NotionPageData.toDomain(): FlashCardNotionPage {
    return FlashCardNotionPage(
        id = NotionId(id!!),
        notionDbID = NotionId(parent?.databaseId!!),
        name = name,
        example = example,
        explanation = explanation,
        coverUrl = urlCover,
        knowLevels = KnowLevels(
            levels = (1..13).map { getKnowLevel(it) }
                .mapNotNull { it }
                .toMap()
        )
    )
}

fun FlashCardNotionPage.toUpdateKnowLevels(): NotionPageData {
    return NotionPageData(
        id = id.get(NotionId.Modifier.AS_IS),
        properties = knowLevels.levels
            .mapKeys { "Know Level ${it.key}" }
            .mapValues { PropertyData(checkbox = it.value) }
    )
}