package org.danceofvalkyries.app.data.telegram_and_notion.sqlite

import org.danceofvalkyries.app.data.telegram_and_notion.SentNotionPageFlashCardToTelegram
import org.danceofvalkyries.app.data.telegram_and_notion.SentNotionPageFlashCardsToTelegram
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class SqlLiteSentNotionPageFlashCardsToTelegram(
    private val connection: Connection,
) : SentNotionPageFlashCardsToTelegram {

    companion object {
        private const val TABLE_NAME = "telegram_message_and_notion_id"
    }

    private val notionIdColumn = TextTableColumn("notion_page_id")
    private val telegramMessageIdColumn = LongTableColumn("telegram_message_id", PrimaryKey())

    override fun iterate(): Sequence<SentNotionPageFlashCardToTelegram> {
        return createStatement().let {
            it.executeQuery(
                SqlQuery {
                    select(notionIdColumn)
                    from(TABLE_NAME)
                }
            )
        }.asSequence()
            .map {
                SqlLiteSentNotionPageFlashCardToTelegram(
                    notionPageId = notionIdColumn.getValue(it)!!,
                    tableName = TABLE_NAME,
                    connection = connection,
                    notionPageIdColumn = notionIdColumn,
                    telegramMessageIdColumn = telegramMessageIdColumn,
                )
            }
    }

    override fun add(
        telegramMessageId: Long,
        notionPageId: String
    ): SentNotionPageFlashCardToTelegram {
        createStatement().execute(
            SqlQuery {
                insert(
                    into = TABLE_NAME,
                    values = listOf(
                        telegramMessageIdColumn to telegramMessageId.toString(),
                        notionIdColumn to notionPageId,
                    )
                )
            }
        )
        return SqlLiteSentNotionPageFlashCardToTelegram(
            notionPageId = notionPageId,
            tableName = TABLE_NAME,
            connection = connection,
            notionPageIdColumn = notionIdColumn,
            telegramMessageIdColumn = telegramMessageIdColumn,
        )
    }

    override fun delete(telegramMessageId: Long) {
        createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        delete()
                        from(TABLE_NAME)
                        where(telegramMessageIdColumn to telegramMessageId.toString())
                    }
                )
            }
    }

    private fun createStatement(): Statement {
        return connection.createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        createIfNotExist(
                            tableName = TABLE_NAME,
                            columns = listOf(telegramMessageIdColumn, notionIdColumn)
                        )
                    }
                )
            }
    }
}