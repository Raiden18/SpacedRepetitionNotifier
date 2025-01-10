package org.danceofvalkyries.notion.api

import notion.api.rest.response.FlashCardResponse
import notion.api.rest.response.NotionDbResponse
import org.danceofvalkyries.notion.api.rest.response.NotionPageResponse
import org.danceofvalkyries.telegram.data.api.TelegramChatApi
import org.danceofvalkyries.telegram.domain.TelegramMessageBody

class NotionDataBaseApiTelegramMessageErrorLoggerDecorator(
    private val notionDataBaseApi: NotionDataBaseApi,
    private val telegramApi: TelegramChatApi,
) : NotionDataBaseApi {

    override suspend fun getDescription(): NotionDbResponse {
        return try {
            notionDataBaseApi.getDescription()
        } catch (throwable: Throwable) {
            val stackTrace = throwable.stackTraceToString()
            telegramApi.sendMessage(
                TelegramMessageBody(
                    text = StringBuilder()
                        .appendLine("Error fetching description:")
                        .appendLine(stackTrace)
                        .toString(),
                    buttons = emptyList()
                )

            )
            throw throwable
        }
    }

    override suspend fun getContent(): List<NotionPageResponse> {
        return try {
            notionDataBaseApi.getContent()
        } catch (throwable: Throwable) {
            val stackTrace = throwable.stackTraceToString()

            telegramApi.sendMessage(
                TelegramMessageBody(
                    text = StringBuilder()
                        .appendLine("Error fetching content:")
                        .appendLine(stackTrace)
                        .toString(),
                    buttons = emptyList()
                )
            )
            throw throwable
        }
    }
}