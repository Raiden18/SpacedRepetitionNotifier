package org.danceofvalkyries.notion.api.models

data class NotionId(
    val rawValue: String
) {

    companion object {
        val EMPTY = NotionId("")
    }

    fun get(): String {
        return rawValue.replace("-", "")
    }
}