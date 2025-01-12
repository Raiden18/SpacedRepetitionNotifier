package org.danceofvalkyries.notion.domain.models

data class NotionDbId(
    private val value: String
) {

    companion object {
        val EMPTY = NotionDbId("")
    }

    val valueId: String
        get() = value
            .replace("-", "")
}