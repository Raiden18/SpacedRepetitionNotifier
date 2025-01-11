package org.danceofvalkyries.notion.domain.models

data class NotionDataBase(
    val id: NotionDbId,
    val name: String,
) {
    companion object {
        val EMPTY = NotionDataBase(
            id = NotionDbId.EMPTY,
            name = "",
        )
    }
}