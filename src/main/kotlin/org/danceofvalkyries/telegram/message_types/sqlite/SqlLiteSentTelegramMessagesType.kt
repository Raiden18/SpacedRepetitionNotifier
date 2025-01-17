package org.danceofvalkyries.telegram.message_types.sqlite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.danceofvalkyries.telegram.message_types.SentTelegramMessageType
import org.danceofvalkyries.telegram.message_types.SentTelegramMessagesType
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asFlow
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class SqlLiteSentTelegramMessagesType(
    private val connection: Connection
) : SentTelegramMessagesType {

    private companion object {
        const val TABLE_NAME = "telegram_messages"
    }

    private val idColumn = LongTableColumn("id", PrimaryKey())
    private val typeColumn = TextTableColumn("type")

    override suspend fun iterate(): Flow<SentTelegramMessageType> {
        return createStatement()
            .let {
                it.executeQuery(
                    SqlQuery {
                        select(idColumn)
                        from(TABLE_NAME)
                    }
                )
            }.asFlow()
            .map {
                SqlLiteSentTelegramMessageType(
                    id = idColumn.getValue(it),
                    tableName = TABLE_NAME,
                    connection = connection,
                    typeColumn = typeColumn,
                    idColumn = idColumn,
                )
            }
    }

    override suspend fun iterate(type: String): Flow<SentTelegramMessageType> {
        return createStatement()
            .let {
                it.executeQuery(
                    SqlQuery {
                        select("*")
                        from(TABLE_NAME)
                        where(typeColumn to type)
                    }
                )
            }.asFlow()
            .map {
                SqlLiteSentTelegramMessageType(
                    id = idColumn.getValue(it),
                    tableName = TABLE_NAME,
                    connection = connection,
                    typeColumn = typeColumn,
                    idColumn = idColumn,
                )
            }
    }

    override suspend fun add(id: Long, type: String): org.danceofvalkyries.telegram.message_types.SentTelegramMessageType {
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
        return SqlLiteSentTelegramMessageType(
            id = id,
            tableName = TABLE_NAME,
            connection = connection,
            typeColumn = typeColumn,
            idColumn = idColumn,
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