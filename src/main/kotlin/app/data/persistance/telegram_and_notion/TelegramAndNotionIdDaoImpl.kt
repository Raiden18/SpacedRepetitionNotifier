package org.danceofvalkyries.app.data.persistance.telegram_and_notion

import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class TelegramAndNotionIdDaoImpl(
    private val connection: Connection
) : TelegramAndNotionIdDao {

    companion object {
        private const val TABLE_NAME = "telegram_message_and_notion_id"
    }

    private val notionIdColumn = TextTableColumn("notion_page_id")
    private val telegramMessageId = LongTableColumn("telegram_message_id", PrimaryKey())

    private val sqlQueries = TgAndNotionSqlQueries(
        tableName = TABLE_NAME,
        notionIdColumn = notionIdColumn,
        telegramMessageId = telegramMessageId,
    )

    override suspend fun getNotionPageIdBy(messageId: Long): NotionId? {
        val resultSet = createTableIfNotExist()
            .executeQuery(sqlQueries.selectBy(messageId))
        val notionPageId = NotionId(
            rawValue = notionIdColumn.getValue(resultSet) ?: return null
        )
        resultSet.close()
        return notionPageId
    }

    override suspend fun getMessageIdBy(notionPageId: NotionId): Long {
        val resultSet = createTableIfNotExist()
            .executeQuery(sqlQueries.selectBy(notionPageId.rawValue))
        val messageId = telegramMessageId.getValue(resultSet)
        resultSet.close()
        return messageId
    }

    override suspend fun save(notionPageId: NotionId, messageId: Long) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(messageId, notionPageId.rawValue)) }
            .also { it.close() }
    }

    override suspend fun deleteBy(messageId: Long) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.deleteBy(messageId)) }
            .close()
    }

    private fun createTableIfNotExist(): Statement {
        return connection.createStatement()
            .also { it.execute(sqlQueries.createTableIfNotExist()) }
    }
}