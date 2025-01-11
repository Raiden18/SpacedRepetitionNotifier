package org.danceofvalkyries.telegram.data.db

import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TableColumn
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

data class TelegramMessagesSqlQueries(
    private val tableName: String,
    private val idColumn: TableColumn,
    private val textColumn: TableColumn,
    private val typeColumn: TextTableColumn,
) {

    fun getAll(): String {
        return SqlQuery {
            select("*")
            from(tableName)
        }
    }

    fun insert(
        telegramMessage: TelegramMessage
    ): String {
        return SqlQuery {
            insert(
                into = tableName,
                values = listOf(
                    idColumn to telegramMessage.id.toString(),
                    textColumn to telegramMessage.body.text,
                    typeColumn to telegramMessage.body.type.toString(),
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
                    textColumn,
                    typeColumn,
                )
            )
        }
    }

    fun update(message: String, messageId: Long): String {
        return SqlQuery {
            update(tableName)
            set(textColumn to message)
            where(idColumn to messageId.toString())
        }
    }
}