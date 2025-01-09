package org.danceofvalkyries.notion.api

import notion.api.rest.response.FlashCardResponse
import notion.api.rest.response.NotionDbResponse
import org.danceofvalkyries.telegram.data.api.TelegramChatApi

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
                StringBuilder()
                    .appendLine("Error fetching description:")
                    .appendLine(stackTrace)
                    .toString()
            )
            throw throwable
        }
    }

    override suspend fun getContent(): List<FlashCardResponse> {
        return try {
            notionDataBaseApi.getContent()
        } catch (throwable: Throwable) {
            val stackTrace = throwable.stackTraceToString()
            telegramApi.sendMessage(
                StringBuilder()
                    .appendLine("Error fetching content:")
                    .appendLine(stackTrace)
                    .toString()
            )
            throw throwable
        }
    }
}