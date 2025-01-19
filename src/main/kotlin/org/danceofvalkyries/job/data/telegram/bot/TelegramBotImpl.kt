package org.danceofvalkyries.job.data.telegram.bot

import org.danceofvalkyries.job.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.job.data.notion.databases.NotionDataBases
import org.danceofvalkyries.job.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.job.data.notion.pages.forget
import org.danceofvalkyries.job.data.notion.pages.recall
import org.danceofvalkyries.job.data.telegram.chat.TelegramChat
import org.danceofvalkyries.job.data.telegram.message.deleteFrom
import org.danceofvalkyries.job.data.telegram.message.edit
import org.danceofvalkyries.job.data.telegram.message.local.*
import org.danceofvalkyries.job.data.telegram.message.sendTo
import org.danceofvalkyries.job.data.telegram.message_types.SentTelegramMessagesType
import org.danceofvalkyries.job.data.telegram.message_types.deleteFrom
import org.danceofvalkyries.job.data.telegram.message_types.saveTo
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.resources.StringResources
import org.danceofvalkyries.utils.scheduler.ActonsSchedulerImpl

class TelegramBotImpl(
    private val telegramChat: TelegramChat,
    private val localDbNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
    private val sentTelegramMessagesType: SentTelegramMessagesType,
    private val onlineDictionaries: OnlineDictionaries,
    private val stringResources: StringResources,
    private val dispatchers: Dispatchers,
) : TelegramBot {

    companion object {
        const val FLASH_CARD_TYPE_MESSAGE = "FLASH_CARD"
        const val NOTIFICATION_TYPE_MESSAGE = "NOTIFICATION"
    }

    private var dbId: String = ""

    override suspend fun editOldNotificationMessageToDoneMessage() {
        editNotificationMessageTo(
            DoneTelegramMessage(stringResources)
        )
    }

    override suspend fun sendNotification() {
        removedTelegramMessages(NOTIFICATION_TYPE_MESSAGE)
        removedCachedMessages(NOTIFICATION_TYPE_MESSAGE)
        sendMessageAndSave(
            NeedRevisingFlashCardMessage(
                getAllFlashCard()
            )
        )
    }

    override suspend fun startRepetitionSessionFor(dbId: String) {
        this.dbId = dbId
        val firstFlashCard = localDbNotionDataBases.getBy(dbId).iterate().first()
        sendFlashCardMessage(firstFlashCard)
    }

    override suspend fun makeForgotten(flashCardId: String) {
        performAction(flashCardId) { it.forget() }
    }

    override suspend fun makeRecalled(flashCardId: String) {
        performAction(flashCardId) { it.recall() }
    }

    override suspend fun deleteMessage(telegramMessageId: Long) {
        telegramChat.delete(telegramMessageId)
    }

    private suspend fun performAction(flashCardId: String, action: (NotionPageFlashCard) -> Map<Int, Boolean>) {
        val restActionsScheduler = ActonsSchedulerImpl(dispatchers)
        val notionDb = localDbNotionDataBases.getBy(dbId)
        val flashCard = notionDb.getPageBy(flashCardId)

        val updatedLevels = action.invoke(flashCard)

        val flashCardsMessagesToDeleteFromTelegram = sentTelegramMessagesType.iterate(FLASH_CARD_TYPE_MESSAGE).toList()
        restActionsScheduler.schedule {
            flashCardsMessagesToDeleteFromTelegram
                .forEach { it.deleteFrom(telegramChat) }
        }
        sentTelegramMessagesType
            .iterate(FLASH_CARD_TYPE_MESSAGE)
            .forEach { it.deleteFrom(sentTelegramMessagesType) }
        notionDb.delete(flashCardId)
        val nextFlashCard = notionDb.iterate().firstOrNull()
        if (nextFlashCard != null) {
            restActionsScheduler.schedule { sendFlashCardMessage(nextFlashCard) }
        }

        val allFlashCards = getAllFlashCard()
        val newNotificationMessage = if (allFlashCards.isEmpty()) {
            DoneTelegramMessage(stringResources)
        } else {
            NeedRevisingFlashCardMessage(allFlashCards)
        }
        restActionsScheduler.schedule { editNotificationMessageTo(newNotificationMessage) }
        restActionsScheduler.schedule {
            restfulNotionDataBases
                .getBy(dbId)
                .getPageBy(flashCardId)
                .setKnowLevels(updatedLevels)
        }
        restActionsScheduler.awaitAll()
    }

    private suspend fun getAllFlashCard(): List<NotionPageFlashCard> {
        return localDbNotionDataBases.iterate()
            .flatMap { it.iterate() }
            .toList()
    }

    private suspend fun NeedRevisingFlashCardMessage(flashCards: List<NotionPageFlashCard>): NeedRevisingNotificationMessage {
        return NeedRevisingNotificationMessage(
            stringResources = stringResources,
            flashCards = flashCards,
            notionDataBases = localDbNotionDataBases.iterate().toList()
        )
    }

    private suspend fun sendMessageAndSave(localMessage: LocalTelegramMessage) {
        val remoteFlashCardMessage = localMessage.sendTo(telegramChat)
        SerializedMessage(
            id = remoteFlashCardMessage.id,
            type = localMessage.type
        ).saveTo(sentTelegramMessagesType)
    }

    private suspend fun removedCachedMessages(type: String) {
        sentTelegramMessagesType.iterate(type)
            .forEach { it.deleteFrom(sentTelegramMessagesType) }
    }

    private suspend fun removedTelegramMessages(type: String) {
        sentTelegramMessagesType.iterate(type)
            .forEach { it.deleteFrom(telegramChat) }
    }

    private suspend fun editNotificationMessageTo(newMessage: LocalTelegramMessage) {
        sentTelegramMessagesType.iterate(NOTIFICATION_TYPE_MESSAGE)
            .forEach { oldNotification ->
                oldNotification.edit(newMessage, telegramChat)
            }
    }

    private suspend fun sendFlashCardMessage(flashCard: NotionPageFlashCard) {
        sendMessageAndSave(
            FlashCardMessage(
                flashCard,
                stringResources,
                onlineDictionaries.iterate(flashCard.notionDbID)
            )
        )
    }
}