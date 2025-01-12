package org.danceofvalkyries.notion.data.repositories.api.models.response

data class NotionDbResponse(
    private val title: List<Title>,
    val id: String
) {

    data class Title(
        val plain_text: String
    )

    val name: String
        get() = title.first().plain_text
}