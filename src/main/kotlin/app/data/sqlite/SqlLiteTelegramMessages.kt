package org.danceofvalkyries.app.data.sqlite

import org.danceofvalkyries.app.domain.telegram.TelegramMessage
import org.danceofvalkyries.app.domain.telegram.TelegramMessages
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class SqlLiteTelegramMessages(
    private val connection: Connection
) : TelegramMessages {

    companion object {
        const val TABLE_NAME = "telegram_messages"
    }

    private val idColumn = LongTableColumn("id", PrimaryKey())
    private val typeColumn = TextTableColumn("type")

    override fun iterate(): Sequence<TelegramMessage> {
        return createTableIfNotExist()
            .let {
                it.executeQuery(
                    SqlQuery {
                        select(idColumn)
                        from(TABLE_NAME)
                    }
                )
            }.asSequence()
            .map {
                SqlLiteTelegramMessage(
                    id = idColumn.getValue(it),
                    tableName = TABLE_NAME,
                    connection = connection,
                    typeColumn = typeColumn,
                )
            }
    }

    override fun add(id: Long, type: String): TelegramMessage {
        createTableIfNotExist()
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
        return SqlLiteTelegramMessage(
            id = id,
            tableName = TABLE_NAME,
            connection = connection,
            typeColumn = typeColumn,
        )
    }

    private fun createTableIfNotExist(): Statement {
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