package org.danceofvalkyries.app.domain.models

// TODO: Remove from TG and Notion
data class Id(
    private val value: String
) {

    companion object {
        val EMPTY = Id("")
    }

    val valueId: String
        get() = value
            .replace("-", "")
}