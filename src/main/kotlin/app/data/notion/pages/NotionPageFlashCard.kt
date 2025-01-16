package org.danceofvalkyries.app.data.notion.pages

interface NotionPageFlashCard {
    val id: String
    val coverUrl: String?
    val notionDbID: String
    val name: String
    val example: String?
    val explanation: String?
    val knowLevels: Map<Int, Boolean>

    fun setKnowLevels(knowLevels: Map<Int, Boolean>)
}