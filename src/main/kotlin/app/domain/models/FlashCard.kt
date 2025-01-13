package org.danceofvalkyries.app.domain.models

import org.danceofvalkyries.app.domain.models.text.Text

data class FlashCard(
    val memorizedInfo: Text,
    val example: Text?,
    val answer: Text?,
    val onlineDictionaries: List<OnlineDictionary>,
    val imageUrl: ImageUrl?,
    val metaInfo: MetaInfo,
) {

    constructor(
        memorizedInfo: String,
        example: String?,
        answer: String?,
        onlineDictionaries: List<OnlineDictionary>,
        imageUrl: ImageUrl?,
        metaInfo: MetaInfo,
    ) : this(
        memorizedInfo = Text(memorizedInfo),
        example = Text(example),
        answer = Text(answer),
        onlineDictionaries = onlineDictionaries,
        imageUrl = imageUrl,
        metaInfo = metaInfo,
    )

    data class MetaInfo(
        val id: String,
        val notionDbId: String,
    )

    companion object {
        val EMPTY = FlashCard(
            memorizedInfo = Text.EMPTY,
            example = Text.EMPTY,
            answer = Text.EMPTY,
            imageUrl = null,
            onlineDictionaries = emptyList(),
            metaInfo = MetaInfo(
                id = "",
                notionDbId = "",
            )
        )
    }
}