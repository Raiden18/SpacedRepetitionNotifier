package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.job.telegram_listener.ButtonAction
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.utils.resources.StringResources

class NeedRevisingNotificationMessage(
    private val stringResources: StringResources,
    private val flashCards: List<NotionPageFlashCard>,
    private val notionDataBases: List<NotionDataBase>
) : NotificationMessage() {

    override fun getText(): String {
        return """${stringResources.flashCardsToRevise(flashCards.count())} 🧠""".trimIndent()
    }

    override fun getNestedButtons(): List<List<TelegramMessage.Button>> {
        return flashCards
            .groupBy { it.notionDbID }
            .map { (dbId, flashCards) ->
                val db = notionDataBases.first { it.getId() == dbId }
                ConstantTelegramMessageButton(
                    text = "${db.getName()}: ${flashCards.count()}",
                    action = TelegramMessage.Button.Action.CallBackData(ButtonAction.DataBase(db.getId()).rawValue),
                )
            }.map { listOf(it) }
    }
}