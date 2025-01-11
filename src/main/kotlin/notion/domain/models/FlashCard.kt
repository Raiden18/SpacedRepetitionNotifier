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

    val memorizedInfoValue: String
        get() = memorizedInfo.withEscapedCharacters()

    val exampleValue: String?
        get() = example?.withEscapedCharacters()

    val answerValue: String?
        get() = answer?.withEscapedCharacters()

    private fun String.withEscapedCharacters(): String {
        return replace("!", "\\!")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("=", "\\=")
            .replace(".", "\\.")
            .replace("_", "\\_")
            .replace("\\\\", "\\")
    }
}