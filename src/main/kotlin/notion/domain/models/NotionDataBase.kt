package org.danceofvalkyries.notion.domain.models

import org.danceofvalkyries.app.domain.models.Id

data class NotionDataBase(
    val id: Id,
    val name: String,
) {
    companion object {
        val EMPTY = NotionDataBase(
            id = Id.EMPTY,
            name = "",
        )
    }
}