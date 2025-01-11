package org.danceofvalkyries.notion.domain.models

data class NotionDbId(
    private val value: String
) {

    companion object {
        val EMPTY = NotionDbId("")
    }

    //TODO move removing - here
    val valueId: String
        get() = this.value
}