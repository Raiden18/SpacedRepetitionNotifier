package org.danceofvalkyries.app.domain.message

sealed class ButtonAction(val rawValue: String) {

    companion object {
        fun parse(rawValue: String): ButtonAction {
            if(rawValue.contains("=").not()) return Unknown // TODO: add unit est
            val parameter = rawValue.split("=")
            val key = parameter[0]
            val value = parameter[1]
            return when (key) {
                "forgottenFlashCardId" -> Forgotten(value)
                "recalledFlashCardId" -> Recalled(value)
                "dbId" -> DataBase(value)
                else -> Unknown
            }
        }
    }

    data class Forgotten(val flashCardId: String) : ButtonAction(
        "forgottenFlashCardId=${flashCardId}"
    )

    data class Recalled(val flashCardId: String) : ButtonAction(
        "recalledFlashCardId=${flashCardId}"
    )

    data class DataBase(val notionDbId: String) : ButtonAction(
        "dbId=${notionDbId}"
    )

    data object Unknown : ButtonAction("Unknown")
}