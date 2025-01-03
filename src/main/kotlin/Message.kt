package org.danceofvalkyries

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup

data class Message(
    private val spacedRepetitionDataBaseGroup: SpacedRepetitionDataBaseGroup
) {

    override fun toString(): String {
        val messageBuilder = MessageBuilder()
            .totalCount(spacedRepetitionDataBaseGroup.totalFlashCardsNeedRevising)
            .emptyLine()
        spacedRepetitionDataBaseGroup.group.forEach { db ->
            messageBuilder
                .dataBase(title = db.name, count = db.flashCardsNeedRevising)
                .deepLink(db.id)
                .emptyLine()
        }
        return messageBuilder.build()
    }
}

private class MessageBuilder {

    private val stringBuilder = StringBuilder()

    fun totalCount(count: Int): MessageBuilder {
        stringBuilder.appendLine("Total count: $count")
        return this
    }

    fun emptyLine(): MessageBuilder {
        stringBuilder.appendLine("")
        return this
    }

    fun dataBase(title: String, count: Int): MessageBuilder {
        stringBuilder.appendLine("$title: $count")
        return this
    }

    fun deepLink(dbId: String): MessageBuilder {
        stringBuilder.appendLine("[Open](https://www.notion.so/databases/$dbId)")
        return this
    }

    fun build(): String {
        return stringBuilder.toString()
    }
}