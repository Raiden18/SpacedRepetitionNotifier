package org.danceofvalkyries.app.data.telegram.users.bot.messages

import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.utils.resources.StringResources

class NeedRevisingFlashCardMessage(
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