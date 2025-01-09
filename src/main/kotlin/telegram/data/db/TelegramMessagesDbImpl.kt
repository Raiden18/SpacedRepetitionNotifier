package org.danceofvalkyries.telegram.data.db

import org.danceofvalkyries.telegram.domain.TelegramMessage
import org.danceofvalkyries.utils.db.TableColumn
import org.danceofvalkyries.utils.db.asSequence
import java.sql.Connection
import java.sql.Statement

class TelegramMessagesDbImpl(
    private val connection: Connection,
) : TelegramMessagesDb {

    private companion object {
        const val TABLE_NAME = "telegram_messages"
    }

    private val idTableColumn = TableColumn.Long(
        name = "id",
        isPrimaryKey = true,
    )
    private val textColumn = TableColumn.Text(
        name = "text",
        isPrimaryKey = false,
    )

    private val sqlQueries = TelegramMessagesSqlQueries(
        tableName = TABLE_NAME,
        idColumn = idTableColumn,
        textColumn = textColumn
    )

    override suspend fun save(telegramMessage: TelegramMessage) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(telegramMessage)) }
            .also { it.close() }
    }

    override suspend fun delete(oldTelegramMessage: TelegramMessage) {
        connection.createStatement()
            .also { it.execute(sqlQueries.delete(oldTelegramMessage.id)) }
            .also { it.close() }
    }

    override suspend fun update(text: String, messageId: Long) {
        connection.createStatement()
            .also { it.execute(sqlQueries.update(text, messageId)) }
            .also { it.close() }
    }

    override suspend fun getAll(): List<TelegramMessage> {
        return createTableIfNotExist()
            .executeQuery(sqlQueries.getAll())
            .asSequence()
            .map {
                TelegramMessage(
                    id = idTableColumn.getValue(it),
                    text = textColumn.getValue(it)
                )
            }.toList()
    }

    private fun createTableIfNotExist(): Statement {
        return connection.createStatement()
            .also { it.execute(sqlQueries.createTableIfNotExist()) }
    }
}