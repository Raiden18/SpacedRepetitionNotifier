package org.danceofvalkyries.app.data.telegram.message_types.sqlite

import org.danceofvalkyries.app.data.telegram.message_types.SentTelegramMessageType
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteSentTelegramMessageType(
    override val id: Long,
    private val tableName: String,
    private val connection: Connection,
    private val typeColumn: TextTableColumn,
) : SentTelegramMessageType {

    override val type: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(typeColumn)
                    from(tableName)
                }
            )?.let(typeColumn::getValue)!!
}