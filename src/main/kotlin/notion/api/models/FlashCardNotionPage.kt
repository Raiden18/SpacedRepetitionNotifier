package org.danceofvalkyries.notion.api.models

data class FlashCardNotionPage(
    val id: NotionId,
    val coverUrl: String?,
    val notionDbID: NotionId,
    val name: String,
    val example: String?,
    val explanation: String?,
    val knowLevels: KnowLevels,
) {

    companion object {
        val EMPTY = FlashCardNotionPage(
            id = NotionId.EMPTY,
            coverUrl = "",
            notionDbID = NotionId.EMPTY,
            name = "",
            example = "",
            explanation = "",
            knowLevels = KnowLevels.EMPTY,
        )
    }

    fun forget(): FlashCardNotionPage {
        return copy(knowLevels = knowLevels.disableAll())
    }

    fun recall(): FlashCardNotionPage {
        return copy(knowLevels = knowLevels.next())
    }
}
