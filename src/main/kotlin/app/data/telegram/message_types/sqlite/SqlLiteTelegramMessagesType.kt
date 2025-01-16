package org.danceofvalkyries.app.data.telegram.message_types.sqlite

import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessageType
import org.danceofvalkyries.app.data.telegram.message_types.TelegramMessagesType
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class SqlLiteTelegramMessagesType(
    private val connection: Connection
) : TelegramMessagesType {

    private companion object {
        const val TABLE_NAME = "telegram_messages"
    }

    private val idColumn = LongTableColumn("id", PrimaryKey())
    private val typeColumn = TextTableColumn("type")

    override suspend fun iterate(): Sequence<TelegramMessageType> {
        return createStatement()
            .let {
                it.executeQuery(
                    SqlQuery {
                        select(idColumn)
                        from(TABLE_NAME)
                    }
                )
            }.asSequence()
            .map {
                SqlLiteTelegramMessageType(
                    id = idColumn.getValue(it),
                    tableName = TABLE_NAME,
                    connection = connection,
                    typeColumn = typeColumn,
                )
            }
    }

    override suspend fun add(id: Long, type: String): TelegramMessageType {
        createStatement()
            .execute(
                SqlQuery {
                    insert(
                        into = TABLE_NAME,
                        values = listOf(
                            idColumn to id.toString(),
                            typeColumn to type
                        )
                    )
                }
            )
        return SqlLiteTelegramMessageType(
            id = id,
            tableName = TABLE_NAME,
            connection = connection,
            typeColumn = typeColumn,
        )
    }

    override suspend fun delete(id: Long) {
        createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        delete()
                        from(TABLE_NAME)
                        where(idColumn to id.toString())
                    }
                )
            }
    }

    private fun createStatement(): Statement {
        return connection.createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        createIfNotExist(
                            tableName = TABLE_NAME,
                            columns = listOf(idColumn, typeColumn)
                        )
                    }
                )
            }
    }
}