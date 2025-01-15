package org.danceofvalkyries.app.data.sqlite.telegram.messages

import org.danceofvalkyries.app.domain.telegram.TelegramMessage
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteTelegramMessage(
    override val id: Long,
    private val tableName: String,
    private val connection: Connection,
    private val typeColumn: TextTableColumn,
) : TelegramMessage {

    override val type: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(typeColumn)
                    from(tableName)
                }
            )?.let(typeColumn::getValue)!!
}