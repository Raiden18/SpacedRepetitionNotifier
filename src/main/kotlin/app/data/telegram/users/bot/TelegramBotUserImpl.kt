package org.danceofvalkyries.app.data.telegram.users.bot

import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.sendMessage
import org.danceofvalkyries.app.data.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessagesType
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.app.domain.message.ButtonAction

class TelegramBotUserImpl(
    private val telegramChat: TelegramChat,
    private val localDbNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
    private val sentTelegramMessagesType: SentTelegramMessagesType,
    private val onlineDictionaries: OnlineDictionaries,
) : TelegramBotUser {

    override suspend fun editOldNotificationMessageToDoneMessage() {
        sentTelegramMessagesType.iterate().forEach { message ->
            telegramChat.edit(
                messageId = message.id,
                newText = """Good Job! 😎 Everything is revised! ✅""",
                newNestedButtons = emptyList()
            )
        }
    }

    override suspend fun deleteOldNotificationMessage() {
        sentTelegramMessagesType.iterate().forEach {
            sentTelegramMessagesType.delete(it.id)
            telegramChat.delete(it.id)
        }
    }

    override suspend fun sendNewNotificationMessage() {
        val flashCards = localDbNotionDataBases.iterate()
            .flatMap { it.iterate() }
            .toList()
        val telegramButtons = flashCards
            .groupBy { it.notionDbID }
            .map { (dbId, flashCards) ->
                val db = localDbNotionDataBases.iterate().first { it.id == dbId }
                ConstantTelegramMessageButton(
                    text = "${db.name}: ${flashCards.count()}",
                    action = TelegramMessage.Button.Action.CallBackData(ButtonAction.DataBase(db.id).rawValue),
                )
            }.map { listOf(it) }
        val sentMessage = telegramChat.sendMessage(
            text = """You have ${flashCards.count()} flashcards to revise 🧠""".trimIndent(),
            imageUrl = null,
            nestedButtons = telegramButtons
        )
        sentTelegramMessagesType.add(
            id = sentMessage.id,
            type = "NOTIFICATION"
        )
    }

    override suspend fun sendFlashCardMessage(flashCard: NotionPageFlashCard) {
        val memorizedInfo = flashCard.name.escapeCharacters()
        val example = flashCard.example?.escapeCharacters()
        val answer = flashCard.explanation?.escapeCharacters()

        val body = StringBuilder()
            .appendLine("*${memorizedInfo}*")
        if (example != null) {
            body.appendLine()
                .appendLine("_${example}_")
        }
        if (answer != null) {
            body.appendLine()
                .appendLine("||${answer}||")
        }
        body.appendLine()
            .append("Choose:")

        val recallActions = listOf(
            ConstantTelegramMessageButton(
                "Forgot  ❌",
                TelegramMessage.Button.Action.CallBackData(ButtonAction.Forgotten(flashCard.id).rawValue)
            ),
            ConstantTelegramMessageButton(
                "Recalled  ✅",
                TelegramMessage.Button.Action.CallBackData(ButtonAction.Recalled(flashCard.id).rawValue)
            )
        )

        val dictionaryTelegramButtons = onlineDictionaries.iterate(flashCard.notionDbID)
            .map {
                ConstantTelegramMessageButton(
                    text = "Look it up",
                    action = TelegramMessage.Button.Action.Url(it.getUrlFor(memorizedInfo)),
                )
            }

        val telegramMessage = telegramChat.sendMessage(
            text = body.toString(),
            imageUrl = flashCard.coverUrl,
            nestedButtons = listOf(
                recallActions,
                dictionaryTelegramButtons
            )
        )


        sentTelegramMessagesType.add(
            telegramMessage.id,
            "FLASH_CARD"
        )
    }

    override suspend fun getAnyFlashCardFor(notionDbId: String): NotionPageFlashCard? {
        return localDbNotionDataBases.iterate()
            .flatMap { it.iterate() }
            .firstOrNull { it.notionDbID == notionDbId }
    }

    override suspend fun updateNotificationMessage() {
        val notificationMessage = sentTelegramMessagesType.iterate().first { it.type == "NOTIFICATION" }

        // TODO: Code duplication from send message. Eliminate copy-pasted code
        val flashCards = localDbNotionDataBases.iterate()
            .flatMap { it.iterate() }
            .toList()
        val telegramButtons = flashCards
            .groupBy { it.notionDbID }
            .map { (dbId, flashCards) ->
                val db = localDbNotionDataBases.iterate().first { it.id == dbId }
                ConstantTelegramMessageButton(
                    text = "${db.name}: ${flashCards.count()}",
                    action = TelegramMessage.Button.Action.CallBackData(ButtonAction.DataBase(db.id).rawValue),
                )
            }.map { listOf(it) }

        telegramChat.edit(
            messageId = notificationMessage.id,
            newText = """You have ${flashCards.count()} flashcards to revise 🧠""".trimIndent(),
            newNestedButtons = telegramButtons
        )
    }

    override suspend fun removeFromDB(flashCardId: String) {
        val flashCard = localDbNotionDataBases.iterate()
            .flatMap { it.iterate() }
            .first { it.id == flashCardId }

        val flashCardDb = localDbNotionDataBases.getBy(flashCard.notionDbID)
        flashCardDb.delete(flashCard.id)
    }

    override suspend fun removeAllFlashCardsFromChat() {
        sentTelegramMessagesType.iterate()
            .filter { it.type == "FLASH_CARD" }
            .forEach {
                println("BOT: ")
                println("REMOVE FROM CHAT: ${it.id}=${it.type}")
                telegramChat.delete(it.id)
            }
    }

    override suspend fun sendNextFlashCardFrom(notionDbId: String) {
        val flashCard = localDbNotionDataBases.iterate()
            .flatMap { it.iterate() }
            .firstOrNull { it.notionDbID == notionDbId }!!
        sendFlashCardMessage(flashCard)
    }

    override suspend fun removeRecalledFlashCardFromLocalDbs(recalledFlashCardID: String) {
        localDbNotionDataBases.iterate().forEach {
            it.delete(recalledFlashCardID)
        }
        sentTelegramMessagesType.iterate().filter {
            it.type == "FLASH_CARD"
        }.forEach {
            sentTelegramMessagesType.delete(it.id)
        }
    }

    override suspend fun removeForgotFlashCardFromLocalDbs(forgotFlashCardId: String) {
        localDbNotionDataBases.iterate().forEach {
            it.delete(forgotFlashCardId)
        }
        sentTelegramMessagesType.iterate().filter {
            it.type == "FLASH_CARD"
        }.forEach {
            sentTelegramMessagesType.delete(it.id)
        }
    }

    private fun String.escapeCharacters(): String {
        return replace("!", "\\!")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("=", "\\=")
            .replace(".", "\\.")
            .replace("_", "\\_")
            .replace("-", "\\-")
            .replace("+", "\\+")
            .replace("\\\\", "\\")
    }
}