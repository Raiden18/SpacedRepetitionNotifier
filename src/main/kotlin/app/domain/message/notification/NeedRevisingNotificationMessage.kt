package org.danceofvalkyries.app.domain.message.notification

import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

data class NeedRevisingNotificationMessage(
    override val id: Long,
    private val flashCards: List<FlashCardNotionPage>,
    private val notionDataBases: List<NotionDataBase>,
) : NotificationMessage() {

    constructor(
        flashCards: List<FlashCardNotionPage>,
        notionDataBases: List<NotionDataBase>,
    ) : this(-1, flashCards, notionDataBases)

    override val telegramBody: TelegramMessageBody
        get() {
            val telegramButtons = flashCards
                .groupBy { it.notionDbID }
                .map { (dbId, flashCards) ->
                    val db = notionDataBases.first { it.id == dbId }
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