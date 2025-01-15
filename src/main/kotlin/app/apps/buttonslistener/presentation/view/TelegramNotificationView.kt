package org.danceofvalkyries.app.apps.buttonslistener.presentation.view

import app.domain.notion.databases.NotionDataBases
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.NotificationView
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.app.domain.message.notification.NotificationMessage
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCards
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.telegram.api.TelegramChatApi

class TelegramNotificationView(
    private val notionDataBases: NotionDataBases,
    private val telegramChatApi: TelegramChatApi,
    private val messages: TelegramMessages,
    private val notionPagesFlashCards: NotionPageFlashCards,
) : NotificationView {

    override suspend fun update() {
        val notificationMessage = messages.iterate().first { it.type == NotificationMessage.TYPE_NAME }
        val flashCards = notionDataBases.iterate()
            .toList()
            .flatMap { notionDb ->
                notionPagesFlashCards.iterate().filter { it.notionDbID == notionDb.id }
            }
            .map {
                FlashCardNotionPage(
                    name = it.name,
                    coverUrl = it.coverUrl,
                    notionDbID = NotionId(it.notionDbID),
                    id = NotionId(it.id),
                    example = it.example,
                    explanation = it.explanation,
                    knowLevels = KnowLevels(it.knowLevels.levels)
                )
            }
            .toList()
        val notionDataBases = notionDataBases.iterate()
            .map {
                NotionDataBase(
                    id = NotionId(it.id),
                    name = it.name
                )
            }.toList()
        telegramChatApi.editInChat(
            NeedRevisingNotificationMessage(
                flashCards = flashCards,
                notionDataBases = notionDataBases
            ).telegramBody,
            notificationMessage.id
        )
    }

    override suspend fun hide() {

    }
}