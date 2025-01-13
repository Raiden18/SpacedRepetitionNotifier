package org.danceofvalkyries.app.data.persistance.notion.database.dao

import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class NotionDataBaseDaoImpl(
    private val connection: Connection
) : NotionDataBaseDao {

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

    override suspend fun insert(notionDataBaseEntity: NotionDataBaseEntity) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(notionDataBaseEntity)) }
            .also { it.close() }
    }

    override suspend fun getAll(): List<NotionDataBaseEntity> {
        return createTableIfNotExist()
            .executeQuery(sqlQueries.selectAll())
            .asSequence()
            .map {
                NotionDataBaseEntity(
                    id = id.getValue(it)!!,
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