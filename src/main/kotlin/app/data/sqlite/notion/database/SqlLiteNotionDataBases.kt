package org.danceofvalkyries.app.data.sqlite.notion.database

import org.danceofvalkyries.app.domain.notion.NotionDataBase
import org.danceofvalkyries.app.domain.notion.NotionDataBases
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class SqlLiteNotionDataBases(
    private val connection: Connection
) : NotionDataBases {

    companion object {
        const val TABLE_NAME = "notion_data_bases"
    }

    private val idColumn = TextTableColumn("id", PrimaryKey())
    private val nameColumn = TextTableColumn("text")

    override suspend fun iterate(): Sequence<NotionDataBase> {
        return createStatement().let {
            it.executeQuery(
                SqlQuery {
                    select(idColumn)
                    from(TABLE_NAME)
                }
            )
        }.asSequence()
            .map {
                SqlLiteNotionDataBase(
                    id = idColumn.getValue(it)!!,
                    tableName = TABLE_NAME,
                    idColumn = idColumn,
                    nameColumn = nameColumn,
                    connection = connection,
                )
            }
    }

    override suspend fun add(
        id: String,
        name: String
    ): NotionDataBase {
        createStatement().execute(
            SqlQuery {
                insert(
                    into = TABLE_NAME,
                    values = listOf(
                        idColumn to id,
                        nameColumn to name
                    )
                )
            }
        )
        return SqlLiteNotionDataBase(
            id = id,
            tableName = TABLE_NAME,
            idColumn = idColumn,
            nameColumn = nameColumn,
            connection = connection,
        )
    }

    override suspend fun clear() {
        createStatement().execute(
            SqlQuery {
                delete()
                from(TABLE_NAME)
            }
        )
    }

    private fun createStatement(): Statement {
        return connection.createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        createIfNotExist(
                            tableName = TABLE_NAME,
                            columns = listOf(idColumn, nameColumn)
                        )
                    }
                )
            }
    }
}