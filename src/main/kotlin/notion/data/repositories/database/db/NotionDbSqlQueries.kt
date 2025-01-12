package org.danceofvalkyries.notion.data.repositories.database.db

import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class NotionDbSqlQueries(
    private val tableName: String,
    private val id: TextTableColumn,
    private val name: TextTableColumn,
) {

    fun insert(notionDataBase: NotionDataBase): String {
        return SqlQuery {
            insert(
                into = tableName,
                values = listOf(
                    id to notionDataBase.id.withoutScore,
                    name to notionDataBase.name,
                )
            )
        }
    }

    fun clear(): String {
        return SqlQuery {
            delete()
            from(tableName)
        }
    }

    fun createIfNotExist(): String {
        return SqlQuery {
            createIfNotExist(
                tableName = tableName,
                columns = listOf(id, name)
            )
        }
    }

    fun selectAll(): String {
        return SqlQuery {
            select("*")
            from(tableName)
        }
    }
}