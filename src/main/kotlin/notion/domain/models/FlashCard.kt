package org.danceofvalkyries.notion.domain.models

data class FlashCard(
    private val memorizedInfo: String,
    private val example: String?,
    private val answer: String?,
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

    fun getMemorizedInfo(formatter: TextFormatter): String? {
        return formatter.format(memorizedInfo)
    }

    fun getExample(formatter: TextFormatter): String? {
        return formatter.format(example)
    }

    fun getAnswer(formatter: TextFormatter): String? {
        return formatter.format(answer)
    }
}