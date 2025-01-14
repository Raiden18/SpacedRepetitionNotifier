package org.danceofvalkyries.app.data.persistance.telegram.messages.dao

import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TableColumn
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

data class TelegramMessagesSqlQueries(
    private val tableName: String,
    private val idColumn: TableColumn,
    private val typeColumn: TextTableColumn,
) {

    fun getAll(): String {
        return SqlQuery {
            select("*")
            from(tableName)
        }
    }

    fun insert(entity: TelegramMessageEntity): String {
        return SqlQuery {
            insert(
                into = tableName,
                values = listOf(
                    idColumn to entity.id.toString(),
                    typeColumn to entity.type,
                )
            )
        }
    }

    fun delete(id: Long): String {
        return SqlQuery {
            delete()
            from(tableName)
            where(idColumn to id.toString())
        }
    }

    fun createTableIfNotExist(): String {
        return SqlQuery {
            createIfNotExist(
                tableName = tableName,
                columns = listOf(
                    idColumn,
                    typeColumn,
                )
            )
        }
    }

    fun selectBy(id: Long): String {
        return SqlQuery {
            select("*")
            from(tableName)
            where(idColumn to id.toString())
        }
    }
}