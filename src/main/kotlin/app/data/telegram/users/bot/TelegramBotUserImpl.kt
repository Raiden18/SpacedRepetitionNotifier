package org.danceofvalkyries.app.data.telegram.users.bot

import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.message.deleteFrom
import org.danceofvalkyries.app.data.telegram.message.edit
import org.danceofvalkyries.app.data.telegram.message.sendTo
import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.message_types.deleteFrom
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.app.data.telegram.users.bot.messages.*
import org.danceofvalkyries.app.data.telegram.users.bot.translator.TextTranslator
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.resources.StringResources

class TelegramBotUserImpl(
    private val telegramChat: TelegramChat,
    private val localDbNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
    private val sentTelegramMessagesType: SentTelegramMessagesType,
    private val onlineDictionaries: OnlineDictionaries,
    private val textTranslator: TextTranslator,
    private val stringResources: StringResources,
) : TelegramBotUser {

    companion object {
        const val FLASH_CARD_TYPE_MESSAGE = "FLASH_CARD"
        const val NOTIFiCATION_TYPE_MESSAGE = "NOTIFICATION"
    }

    override suspend fun editOldNotificationMessageToDoneMessage() {
        editNotificationMessageTo(
            DoneTelegramMessage(stringResources)
        )
    }

    override suspend fun deleteOldNotificationMessage() {
        sentTelegramMessagesType.iterate(NOTIFiCATION_TYPE_MESSAGE)
            .forEach { it.deleteFrom(telegramChat) }
        removedCachedMessages(NOTIFiCATION_TYPE_MESSAGE)
    }

    override suspend fun sendNewNotificationMessage() {
        sentMessageAndSave(
            NeedRevisingFlashCardMessage(
                getAllFlashCard()
            )
        )
    }

    override suspend fun sendFlashCardMessage(flashCard: NotionPageFlashCard) {
        sentMessageAndSave(
            FlashCardMessage(
                flashCard,
                textTranslator,
                stringResources,
                onlineDictionaries.iterate(flashCard.notionDbID)
            )
        )
    }

    override suspend fun getAnyFlashCardFor(notionDbId: String): NotionPageFlashCard? {
        return localDbNotionDataBases.iterate()
            .flatMap { it.iterate() }
            .firstOrNull { it.notionDbID == notionDbId }
    }

    override suspend fun updateNotificationMessage() {
        val flashCards = getAllFlashCard()
        if (flashCards.isEmpty()) {
            editOldNotificationMessageToDoneMessage()
            return
        }
        editNotificationMessageTo(
            NeedRevisingFlashCardMessage(flashCards)
        )
    }

    override suspend fun deleteAllFlashCardsFromChat() {
        sentTelegramMessagesType.iterate(FLASH_CARD_TYPE_MESSAGE)
            .forEach { it.deleteFrom(telegramChat) }
    }

    override suspend fun sendNextFlashCardFrom(notionDbId: String) {
        val flashCard = getAllFlashCard()
            .firstOrNull { it.notionDbID == notionDbId }
        if (flashCard != null) {
            sendFlashCardMessage(flashCard)
        }
    }

    override suspend fun removeRecalledFlashCardFromLocalDbs(recalledFlashCardID: String) {
        localDbNotionDataBases.iterate().forEach {
            it.delete(recalledFlashCardID)
        }
        removedCachedMessages(FLASH_CARD_TYPE_MESSAGE)
    }

    override suspend fun removeForgotFlashCardFromLocalDbs(forgotFlashCardId: String) {
        localDbNotionDataBases.iterate().forEach {
            it.delete(forgotFlashCardId)
        }
        removedCachedMessages(FLASH_CARD_TYPE_MESSAGE)
    }

    override suspend fun makeForgottenOnNotion(flashCardId: String) {
        val flashCard = getAllFlashCard()
            .map {
                FlashCardNotionPage(
                    name = it.name,
                    coverUrl = it.coverUrl,
                    notionDbID = NotionId(it.notionDbID),
                    id = NotionId(it.id),
                    example = it.example,
                    explanation = it.explanation,
                    knowLevels = KnowLevels(it.knowLevels)
                )
            }.first { it.id.rawValue == flashCardId }
        val forgottenFlashCard = flashCard.forget()
        val restfullDataBase = restfulNotionDataBases.getBy(flashCard.notionDbID.rawValue)
        restfullDataBase.getPageBy(flashCardId).setKnowLevels(forgottenFlashCard.knowLevels.levels)
    }

    override suspend fun makeRecalledOnNotion(flashCardId: String) {
        val flashCard = getAllFlashCard()
            .map {
                FlashCardNotionPage(
                    name = it.name,
                    coverUrl = it.coverUrl,
                    notionDbID = NotionId(it.notionDbID),
                    id = NotionId(it.id),
                    example = it.example,
                    explanation = it.explanation,
                    knowLevels = KnowLevels(it.knowLevels)
                )
            }.first { it.id.rawValue == flashCardId }

        val recalled = flashCard.recall()
        val restfullDataBase = restfulNotionDataBases.getBy(flashCard.notionDbID.rawValue)
        restfullDataBase.getPageBy(flashCardId).setKnowLevels(recalled.knowLevels.levels)
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

    private suspend fun sentMessageAndSave(localMessage: LocalTelegramMessage) {
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

    private suspend fun editNotificationMessageTo(newMessage: LocalTelegramMessage) {
        sentTelegramMessagesType.iterate(NOTIFiCATION_TYPE_MESSAGE)
            .forEach { oldNotification ->
                oldNotification.edit(newMessage, telegramChat)
            }
    }
}