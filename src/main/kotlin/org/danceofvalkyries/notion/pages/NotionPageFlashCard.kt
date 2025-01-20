package org.danceofvalkyries.notion.pages

interface NotionPageFlashCard {
    suspend fun getId(): String
    suspend fun getCoverUrl(): String?
    suspend fun getNotionDbId(): String
    suspend fun getName(): String
    suspend fun getExample(): String?
    suspend fun getExplanation(): String?
    suspend fun getKnowLevels(): Map<Int, Boolean>
    suspend fun setKnowLevels(knowLevels: Map<Int, Boolean>)
}

suspend fun NotionPageFlashCard.recall(): Map<Int, Boolean> {
    val knowLevels = getKnowLevels()
    val nextChecked = knowLevels.keys.firstOrNull { knowLevels[it] == false } ?: return knowLevels
    return knowLevels.mapValues { it.key <= nextChecked }
}

suspend fun NotionPageFlashCard.forget(): Map<Int, Boolean> {
    return getKnowLevels().mapValues { false }
}