package org.danceofvalkyries.app.domain.message.notification

import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

data class NeedRevisingNotificationMessage(
    override val id: Long,
    private val notionDataBases: NotionDataBases,
) : NotificationMessage() {

    constructor(
        notionDataBases: NotionDataBases,
    ) : this(-1, notionDataBases)

    override suspend fun asTelegramBody(): TelegramMessageBody {
        val flashCards = notionDataBases.iterate()
            .flatMap { it.iterate() }
            .toList()
        val telegramButtons = flashCards
            .groupBy { it.notionDbID }
            .map { (dbId, flashCards) ->
                val db = notionDataBases.iterate().first { it.id == dbId }
                TelegramButton(
                    text = "${db.name}: ${flashCards.count()}",
                    action = TelegramButton.Action.CallBackData(ButtonAction.DataBase(db.id).rawValue),
                )
            }

        return TelegramMessageBody(
            text = """You have ${flashCards.count()} flashcards to revise 🧠""".trimIndent(),
            telegramButtons = telegramButtons,
            telegramImageUrl = null,
        )
    }
}