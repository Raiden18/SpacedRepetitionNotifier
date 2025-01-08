package org.danceofvalkyries.message

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup

val revisingIsNeededMessage: (SpacedRepetitionDataBaseGroup) -> String = {
    val messageBuilder = MessageBuilder()
        .totalCount(it.totalFlashCardsNeedRevising)
        .emptyLine()
    it.group.forEach { db ->
        messageBuilder
            .dataBase(title = db.name, count = db.flashCardsNeedRevising)
            .deepLink(db.id)
            .emptyLine()
    }
    messageBuilder.build()
}

val goodJobMessage: () -> String = {
    "Good Job! \uD83D\uDE0E Everything is revised! ✅"
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

