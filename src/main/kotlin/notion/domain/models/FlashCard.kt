package org.danceofvalkyries.notion.domain.models

data class FlashCard(
    val memorizedInfo: String,
    val example: String?,
    val answer: String?,
    val imageUrl: ImageUrl?,
    val metaInfo: MetaInfo,
) {

    data class MetaInfo(
        val id: String,
        val notionDbId: NotionDbId,
    )

    companion object {
        val EMPTY = FlashCard(
            memorizedInfo = "",
            example = "",
            answer = "",
            imageUrl = null,
            metaInfo = MetaInfo(
                id = "",
                notionDbId = NotionDbId.EMPTY,
            )
        )
    }
}