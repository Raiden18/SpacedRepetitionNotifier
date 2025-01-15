package org.danceofvalkyries.app.data.persistance.telegram_and_notion

import org.danceofvalkyries.notion.api.models.NotionId

interface TelegramAndNotionIdDao {
    suspend fun getNotionPageIdBy(messageId: Long): NotionId?
    suspend fun getMessageIdBy(notionPageId: NotionId): Long
    suspend fun save(notionPageId: NotionId, messageId: Long)
    suspend fun deleteBy(messageId: Long)
}