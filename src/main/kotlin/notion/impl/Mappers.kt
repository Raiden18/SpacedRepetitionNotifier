package org.danceofvalkyries.notion.impl

import notion.impl.client.models.NotionPageData
import notion.impl.client.models.PropertyData
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

fun NotionPageData.toDomain(): FlashCardNotionPage {
    return FlashCardNotionPage(
        id = NotionId(id!!),
        notionDbID = NotionId(NotionId(parent?.databaseId!!).get()),
        name = name,
        example = example,
        explanation = explanation,
        coverUrl = coverUrl,
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