package org.danceofvalkyries.notion.domain.models

// TODO: add strategy to access id
data class NotionId(
    val rawValue: String
) {

    companion object {
        val EMPTY = NotionId("")
    }

    val withoutScore: String
        get() = rawValue.replace("-", "")

}