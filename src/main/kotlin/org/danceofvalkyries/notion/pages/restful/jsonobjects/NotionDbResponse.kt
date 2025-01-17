package org.danceofvalkyries.notion.pages.restful.jsonobjects

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