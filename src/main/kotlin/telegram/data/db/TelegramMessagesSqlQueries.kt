package org.danceofvalkyries.telegram.data.db

import org.danceofvalkyries.telegram.domain.TelegramMessage
import org.danceofvalkyries.utils.db.SqlQueryBuilder
import org.danceofvalkyries.utils.db.tables.columns.TableColumn

data class TelegramMessagesSqlQueries(
    private val tableName: String,
    private val idColumn: TableColumn,
    private val textColumn: TableColumn,
) {

    fun getAll(): String {
        return SqlQueryBuilder()
            .select("*")
            .from(tableName)
            .build()
    }

    fun insert(
        telegramMessage: TelegramMessage
    ): String {
        return SqlQueryBuilder()
            .insert(
                into = tableName,
                values = listOf(
                    idColumn to telegramMessage.id.toString(),
                    textColumn to telegramMessage.text,
                )
            )
            .build()
    }

    fun delete(id: Long): String {
        return SqlQueryBuilder()
            .delete()
            .from(tableName)
            .where(idColumn to id.toString())
            .build()
    }

    fun createTableIfNotExist(): String {
        return SqlQueryBuilder()
            .createIfNotExist(
                tableName = tableName,
                columns = listOf(
                    idColumn,
                    textColumn,
                )
            )
            .build()
    }

    fun update(message: String, messageId: Long): String {
        return SqlQueryBuilder()
            .update(tableName)
            .set(textColumn to message)
            .where(idColumn to messageId.toString())
            .build()
    }
}