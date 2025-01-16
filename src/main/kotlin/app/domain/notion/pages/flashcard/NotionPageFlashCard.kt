package org.danceofvalkyries.app.domain.notion.pages.flashcard

interface NotionPageFlashCard {
    val id: String
    val coverUrl: String?
    val notionDbID: String
    val name: String
    val example: String?
    val explanation: String?
    val knowLevels: KnowLevels

    interface KnowLevels {
        val levels: Map<Int, Boolean>
    }

    fun setKnowLevels(knowLevels: KnowLevels)
}