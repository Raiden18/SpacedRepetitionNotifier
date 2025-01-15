package org.danceofvalkyries.app.apps

import com.google.gson.Gson
import org.danceofvalkyries.app.App
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.data.sqlite.notion.pages.flashcard.SqlLiteNotionPageFlashCards
import org.danceofvalkyries.app.data.sqlite.telegram.messages.SqlLiteTelegramMessages
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.Dispatchers

class SandBoxApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
) : App {

    @Suppress("UNREACHABLE_CODE")
    override suspend fun run() {
        val telegramMessages: TelegramMessages = SqlLiteTelegramMessages(environment.dataBase.establishConnection())

        telegramMessages.iterate().forEach {
            println(it.id)
            println(it.type)
        }
    }
}