package org.danceofvalkyries.notion.impl

import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId
import notion.impl.client.models.NotionPageData
import notion.impl.client.models.PropertyData

fun NotionPageData.toDomain(): FlashCardNotionPage {
    return FlashCardNotionPage(
        id = NotionId(id!!),
        notionDbID = NotionId(parent?.databaseId!!),
        name = name,
        example = example,
        explanation = explanation,
        coverUrl = urlCover,
        knowLevels = KnowLevels(
            levels = (1..13).associate { getKnowLevel(it) }
                .filterValues { it != null }
                .mapValues { it.value!! }
        )
    )
}

fun FlashCardNotionPage.toUpdateKnowLevels(): NotionPageData {
    return NotionPageData(
        id = id.rawValue,
        properties = knowLevels.levels
            .filterValues { it != null }
            .mapKeys { "Know Level ${it.key}" }
            .mapValues { PropertyData(checkbox = it.value) }
    )
}