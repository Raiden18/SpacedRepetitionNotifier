package org.danceofvalkyries.notion.pages

interface NotionPageFlashCard {
    val id: String
    val coverUrl: String?
    val notionDbID: String
    val name: String
    val example: String?
    val explanation: String?
    val knowLevels: Map<Int, Boolean>
}

fun NotionPageFlashCard.recall(): Map<Int, Boolean> {
    val nextChecked = knowLevels.keys.firstOrNull { knowLevels[it] == false } ?: return knowLevels
    return knowLevels.mapValues { it.key <= nextChecked }
}

fun NotionPageFlashCard.forget(): Map<Int, Boolean> {
    return knowLevels.mapValues { false }
}