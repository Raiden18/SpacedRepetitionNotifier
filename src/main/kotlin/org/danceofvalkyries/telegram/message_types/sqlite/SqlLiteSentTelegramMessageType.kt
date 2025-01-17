package org.danceofvalkyries.telegram.message_types.sqlite

import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteSentTelegramMessageType(
    private val id: Long,
    private val idColumn: LongTableColumn,
    private val tableName: String,
    private val connection: Connection,
    private val typeColumn: TextTableColumn,
) : org.danceofvalkyries.telegram.message_types.SentTelegramMessageType {

    override val type: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(typeColumn)
                    from(tableName)
                    where(idColumn to id.toString())
                }
            )?.let(typeColumn::getValue)!!

    override fun getId(): Long {
        return id
    }

    override fun getText(): String {
        error("Is not saved in DB")
    }

    override fun getImageUrl(): String? {
        error("Is not saved in DB")
    }

    override suspend fun getNestedButtons(): List<List<TelegramMessage.Button>> {
        error("Is not saved in DB")
    }
}