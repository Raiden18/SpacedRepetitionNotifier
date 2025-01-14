package org.danceofvalkyries.app.data.persistance.telegram_and_notion

import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class TgAndNotionSqlQueries(
    private val tableName: String,
    private val notionIdColumn: TextTableColumn,
    private val telegramMessageId: LongTableColumn,
) {

    fun createTableIfNotExist(): String {
        return SqlQuery {
            createIfNotExist(
                tableName = tableName,
                columns = listOf(telegramMessageId, notionIdColumn)
            )
        }
    }

    fun insert(
        messageId: Long,
        notionPageId: String
    ): String {
        return SqlQuery {
            insert(
                into = tableName,
                values = listOf(
                    telegramMessageId to messageId.toString(),
                    notionIdColumn to notionPageId,
                )
            )
        }
    }

    fun selectBy(messageId: Long): String {
        return SqlQuery {
            select("*")
            from(tableName)
            where(telegramMessageId to messageId.toString())
        }
    }

    fun selectBy(notionPageId: String): String {
        return SqlQuery {
            select("*")
            from(tableName)
            where(notionIdColumn to notionPageId)
        }
    }

    fun deleteBy(messageId: Long): String {
        return SqlQuery {
            delete()
            from(tableName)
            where(telegramMessageId to messageId.toString())
        }
    }
}