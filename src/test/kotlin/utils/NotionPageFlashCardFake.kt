package utils

import org.danceofvalkyries.notion.pages.NotionPageFlashCard

data class NotionPageFlashCardFake(
    private val id: String = "",
    private val coverUrl: String? = null,
    private val notionDbID: String = "",
    private val name: String = "",
    private val example: String? = null,
    private val explanation: String? = null,
    private val knowLevels: Map<Int, Boolean> = emptyMap(),
) : NotionPageFlashCard {

    override suspend fun getId(): String {
        return id
    }

    override suspend fun getCoverUrl(): String? {
        return coverUrl
    }

    override suspend fun getNotionDbId(): String {
        return notionDbID
    }

    override suspend fun getName(): String {
        return name
    }

    override suspend fun getExample(): String? {
        return example
    }

    override suspend fun getExplanation(): String? {
        return explanation
    }

    override suspend fun getKnowLevels(): Map<Int, Boolean> {
        return knowLevels
    }

    override suspend fun setKnowLevels(knowLevels: Map<Int, Boolean>) = Unit
}