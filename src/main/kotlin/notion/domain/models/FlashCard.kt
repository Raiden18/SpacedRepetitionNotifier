package org.danceofvalkyries.notion.domain.models

data class FlashCard(
    val memorizedInfo: String,
    val example: String?,
    val answer: String?,
    val imageUrl: ImageUrl?
) {

    constructor(
        memorizedInfo: String,
        example: String?,
        answer: String?,
        imageUrl: String?
    ) : this(
        memorizedInfo,
        example,
        answer,
        imageUrl.takeIf { it != null }?.let(::ImageUrl)
    )
}