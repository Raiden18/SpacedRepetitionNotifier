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

    override val text: String
        get() = """${stringResources.flashCardsToRevise(flashCards.count())} 🧠""".trimIndent()

    override val nestedButtons: List<List<TelegramMessage.Button>>
        get() = flashCards
            .groupBy { it.notionDbID }
            .map { (dbId, flashCards) ->
                val db = notionDataBases.first { it.id == dbId }
                ConstantTelegramMessageButton(
                    text = "${db.name}: ${flashCards.count()}",
                    action = TelegramMessage.Button.Action.CallBackData(ButtonAction.DataBase(db.id).rawValue),
                )
            }.map { listOf(it) }
}