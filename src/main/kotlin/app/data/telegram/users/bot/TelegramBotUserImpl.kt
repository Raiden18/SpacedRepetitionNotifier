package org.danceofvalkyries.app.data.telegram.users.bot

import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.telegram.chat.TelegramChat
import org.danceofvalkyries.app.data.telegram.chat.sendMessage
import org.danceofvalkyries.app.data.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessagesType
import org.danceofvalkyries.app.data.telegram.users.TelegramBotUser
import org.danceofvalkyries.app.domain.message.ButtonAction

class TelegramBotUserImpl(
    private val telegramChat: TelegramChat,
    private val notionDataBases: NotionDataBases,
    private val telegramMessagesType: TelegramMessagesType,
    private val onlineDictionaries: OnlineDictionaries,
) : TelegramBotUser {

    override suspend fun editOldNotificationMessageToDoneMessage() {
        telegramMessagesType.iterate().forEach { message ->
            val telegramMessage = telegramChat.getMessage(messageId = message.id)
            telegramMessage.edit(
                newText = """Good Job! 😎 Everything is revised! ✅""",
                newImageUrl = null,
                newNestedButtons = emptyList(),
            )
        }
    }

    override suspend fun deleteOldNotificationMessage() {
        telegramMessagesType.iterate().forEach {
            telegramMessagesType.delete(it.id)
            telegramChat.delete(messageId = it.id)
        }
    }

    override suspend fun sendNewNotificationMessage() {
        val flashCards = notionDataBases.iterate()
            .flatMap { it.iterate() }
            .toList()
        val telegramButtons = flashCards
            .groupBy { it.notionDbID }
            .map { (dbId, flashCards) ->
                val db = notionDataBases.iterate().first { it.id == dbId }
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
        telegramMessagesType.add(
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

        telegramMessagesType.add(
            telegramMessage.id,
            "FLASH_CARD"
        )
    }

    override suspend fun removeFlashCards() {
        telegramMessagesType.iterate().filter { it.type == "FLASH_CARD" }
            .forEach {
                telegramChat.delete(it.id)
                telegramMessagesType.delete(it.id)
            }
    }

    override suspend fun updateNotificationMessage() {
        val notificationMessage = telegramMessagesType.iterate().first { it.type == "NOTIFICATION" }

        // TODO: Code duplication from send message. Eliminate copy-pasted code
        val flashCards = notionDataBases.iterate()
            .flatMap { it.iterate() }
            .toList()
        val telegramButtons = flashCards
            .groupBy { it.notionDbID }
            .map { (dbId, flashCards) ->
                val db = notionDataBases.iterate().first { it.id == dbId }
                ConstantTelegramMessageButton(
                    text = "${db.name}: ${flashCards.count()}",
                    action = TelegramMessage.Button.Action.CallBackData(ButtonAction.DataBase(db.id).rawValue),
                )
            }.map { listOf(it) }

        telegramChat.getMessage(notificationMessage.id)
            .edit(
                newText = """You have ${flashCards.count()} flashcards to revise 🧠""".trimIndent(),
                newImageUrl = null,
                newNestedButtons = telegramButtons
            )
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