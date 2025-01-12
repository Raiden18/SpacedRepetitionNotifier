package org.danceofvalkyries.notion.domain.models

data class NotionDataBase(
    val id: NotionId,
    val name: String,
) {
    companion object {
        val EMPTY = NotionDataBase(
            id = NotionId.EMPTY,
            name = "",
        )
    }
}