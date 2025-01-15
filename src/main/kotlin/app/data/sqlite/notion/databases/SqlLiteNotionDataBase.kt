package app.data.sqlite.notion.databases

import app.domain.notion.databases.NotionDataBase
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteNotionDataBase(
    override val id: String,
    private val tableName: String,
    private val idColumn: TextTableColumn,
    private val nameColumn: TextTableColumn,
    private val connection: Connection,
) : NotionDataBase {

    override val name: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(nameColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(nameColumn::getValue)!!
}