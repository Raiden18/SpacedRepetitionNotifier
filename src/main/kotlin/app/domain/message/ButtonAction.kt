package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.api.models.NotionId

sealed class ButtonAction(val rawValue: String) {

    companion object {
        fun parse(rawValue: String): ButtonAction {
            val parameter = rawValue.split("=")
            val key = parameter[0]
            val value = parameter[1]
            return when (key) {
                "forgottenFlashCardId" -> Forgotten(NotionId(value))
                "recalledFlashCardId" -> Recalled(NotionId(value))
                "dbId" -> DataBase(NotionId(value))
                else -> error("Unknown callback action: $rawValue")
            }
        }
    }

    data class Forgotten(val flashCardId: NotionId) : ButtonAction(
        "forgottenFlashCardId=${flashCardId.rawValue}"
    )

    data class Recalled(val flashCardId: NotionId) : ButtonAction(
        "recalledFlashCardId=${flashCardId.rawValue}"
    )

    data class DataBase(val notionDbId: NotionId) : ButtonAction(
        "dbId=${notionDbId.rawValue}"
    )
}