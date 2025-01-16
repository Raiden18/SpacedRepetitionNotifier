package org.danceofvalkyries.app.data.telegram.message_types.sqlite

import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessageType
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteTelegramMessageType(
    override val id: Long,
    private val tableName: String,
    private val connection: Connection,
    private val typeColumn: TextTableColumn,
) : TelegramMessageType {

    override val type: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(typeColumn)
                    from(tableName)
                }
            )?.let(typeColumn::getValue)!!
}