package org.danceofvalkyries.notion.databases.sqlite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.databases.NotionDataBases
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asFlow
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class SqlLiteNotionDataBases(
    private val connection: Connection
) : NotionDataBases {

    private companion object {
        const val TABLE_NAME = "notion_data_bases"
    }

    private val idColumn = TextTableColumn("id", PrimaryKey())
    private val nameColumn = TextTableColumn("text")

    override suspend fun iterate(): Flow<NotionDataBase> {
        return createStatement().let {
            it.executeQuery(
                SqlQuery {
                    select(idColumn)
                    from(TABLE_NAME)
                }
            )
        }.asFlow().map { getBy(idColumn.getValue(it)!!) }
    }

    override fun getBy(id: String): NotionDataBase {
        return SqlLiteNotionDataBase(
            id = id,
            tableName = TABLE_NAME,
            idColumn = idColumn,
            nameColumn = nameColumn,
            connection = connection,
        )
    }

    override suspend fun add(notionDataBase: NotionDataBase): NotionDataBase {
        val id = notionDataBase.getId()
        val name = notionDataBase.getName()
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
        val sqlLiteDb = SqlLiteNotionDataBase(
            id = id,
            tableName = TABLE_NAME,
            idColumn = idColumn,
            nameColumn = nameColumn,
            connection = connection,
        )
        notionDataBase.iterate().collect {
            sqlLiteDb.add(it)
        }
        return sqlLiteDb
    }

    override suspend fun clear() {
        iterate().collect { it.clear() }
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