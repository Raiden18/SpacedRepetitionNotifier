package org.danceofvalkyries.notion.data.repositories.db.table

import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class FlashCardsTablesSqlQueries(
    private val tableName: String,
    private val id: TextTableColumn,
    private val name: TextTableColumn,
) {

    fun insert(notionDataBase: NotionDataBase): String {
        return SqlQuery {
            insert(
                into = tableName,
                values = listOf(
                    id to notionDataBase.id.valueId,
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