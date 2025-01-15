package org.danceofvalkyries.app.data.sqlite.notion.database

import org.danceofvalkyries.app.domain.notion.NotionDataBase
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteNotionDataBase(
    override val id: String,
    private val tableName: String,
    private val nameColumn: TextTableColumn,
    private val connection: Connection,
) : NotionDataBase {

    override val name: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(nameColumn)
                    from(tableName)
                }
            )?.let(nameColumn::getValue)!!
}