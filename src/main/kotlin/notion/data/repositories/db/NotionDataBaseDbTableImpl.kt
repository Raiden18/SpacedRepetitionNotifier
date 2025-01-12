package org.danceofvalkyries.notion.data.repositories.db

import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class NotionDataBaseDbTableImpl(
    private val connection: Connection
) : NotionDataBaseDbTable {

    private companion object {
        const val TABLE_NAME = "notion_data_bases"
    }

    private val id = TextTableColumn("id", PrimaryKey())
    private val name = TextTableColumn("text")

    private val sqlQueries = NotionDbSqlQueries(
        tableName = TABLE_NAME,
        id = id,
        name = name,
    )

    override suspend fun insert(notionDataBase: NotionDataBase) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(notionDataBase)) }
            .also { it.close() }
    }

    override suspend fun getAll(): List<NotionDataBase> {
        return createTableIfNotExist()
            .executeQuery(sqlQueries.selectAll())
            .asSequence()
            .map {
                NotionDataBase(
                    id = Id(id.getValue(it)!!),
                    name = name.getValue(it)!!,
                )
            }.toList()
    }

    override suspend fun clear() {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.clear()) }
            .also { it.close() }
    }

    private fun createTableIfNotExist(): Statement {
        return connection.createStatement()
            .also { it.execute(sqlQueries.createIfNotExist()) }
    }
}