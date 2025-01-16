package org.danceofvalkyries.app.data.telegram_and_notion.sqlite

import org.danceofvalkyries.app.data.telegram_and_notion.SentNotionPageFlashCardToTelegram
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteSentNotionPageFlashCardToTelegram(
    override val notionPageId: String,
    private val tableName: String,
    private val connection: Connection,
    private val notionPageIdColumn: TextTableColumn,
    private val telegramMessageIdColumn: LongTableColumn
) : SentNotionPageFlashCardToTelegram {

    override val messageId: Long
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(telegramMessageIdColumn)
                    from(tableName)
                    where(notionPageIdColumn to notionPageId)
                }
            )?.let(telegramMessageIdColumn::getValue)!!
}