package org.danceofvalkyries.app.data.persistance.telegram.messages.dao

import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.ResultSet

data class TelegramMessageEntity(
    val id: Long,
    val type: String?
) {
    constructor(
        resultSet: ResultSet,
        idTableColumn: LongTableColumn,
        typeColumn: TextTableColumn,
    ): this(
        id = idTableColumn.getValue(resultSet),
        type = typeColumn.getValue(resultSet),
    )
}