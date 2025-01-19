package org.danceofvalkyries.app.data.telegram.bot

import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.notion.pages.forget
import org.danceofvalkyries.app.data.notion.pages.recall
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.message.deleteFrom
import org.danceofvalkyries.app.data.telegram.message.edit
import org.danceofvalkyries.app.data.telegram.message.local.*
import org.danceofvalkyries.app.data.telegram.message.local.translator.TextTranslator
import org.danceofvalkyries.app.data.telegram.message.sendTo
import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.message_types.deleteFrom
import org.danceofvalkyries.app.data.telegram.message_types.saveTo
import org.danceofvalkyries.utils.resources.StringResources

class TelegramBotImpl(
    private val telegramChat: TelegramChat,
    private val localDbNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
    private val sentTelegramMessagesType: SentTelegramMessagesType,
    private val onlineDictionaries: OnlineDictionaries,
    private val textTranslator: TextTranslator,
    private val stringResources: StringResources,
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
        val firstFlashCard = getAllFlashCard().first { it.notionDbID == dbId }
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
        val flashCard = getAllFlashCard().first { it.id == flashCardId }
        val updatedLevels = action.invoke(flashCard)
        removedTelegramMessages(FLASH_CARD_TYPE_MESSAGE)
        removedCachedMessages(FLASH_CARD_TYPE_MESSAGE)
        removeNotionFlashCard(flashCardId)
        sendNextFlashCardFrom(dbId)
        updateNotificationMessage()
        restfulNotionDataBases
            .getBy(flashCard.notionDbID)
            .getPageBy(flashCardId)
            .setKnowLevels(updatedLevels)
    }

    private suspend fun getAllFlashCard(): List<NotionPageFlashCard> {
        return localDbNotionDataBases.iterate()
            .flatMap { it.iterate() }
            .toList()
    }

    private suspend fun NeedRevisingFlashCardMessage(flashCards: List<NotionPageFlashCard>): NeedRevisingFlashCardMessage {
        return NeedRevisingFlashCardMessage(
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

    private suspend fun removeNotionFlashCard(notionPagId: String) {
        localDbNotionDataBases.iterate().forEach {
            it.delete(notionPagId)
        }
    }

    private suspend fun sendFlashCardMessage(flashCard: NotionPageFlashCard) {
        sendMessageAndSave(
            FlashCardMessage(
                flashCard,
                textTranslator,
                stringResources,
                onlineDictionaries.iterate(flashCard.notionDbID)
            )
        )
    }

    private suspend fun updateNotificationMessage() {
        val flashCards = getAllFlashCard()
        val newNotificationMessage = if (flashCards.isEmpty()) {
            DoneTelegramMessage(stringResources)
        } else {
            NeedRevisingFlashCardMessage(flashCards)
        }
        editNotificationMessageTo(newNotificationMessage)
    }

    private suspend fun sendNextFlashCardFrom(notionDbId: String) {
        val flashCard = localDbNotionDataBases.getBy(notionDbId)
            .iterate()
            .firstOrNull()
        if (flashCard != null) {
            sendFlashCardMessage(flashCard)
        }
    }
}