package org.danceofvalkyries.app.domain.models

import org.danceofvalkyries.telegram.domain.models.TelegramImageUrl

data class FlashCard(
    val memorizedInfo: String,
    val example: String?,
    val answer: String?,
    val onlineDictionaries: List<OnlineDictionary>,
    val telegramImageUrl: TelegramImageUrl?,
    val metaInfo: MetaInfo,
) {

    data class MetaInfo(
        val id: String,
        val notionDbId: String,
    )

    companion object {
        val EMPTY = FlashCard(
            memorizedInfo = "",
            example = "",
            answer = "",
            telegramImageUrl = null,
            onlineDictionaries = emptyList(),
            metaInfo = MetaInfo(
                id = "",
                notionDbId = "",
            )
        )
    }
}