package org.danceofvalkyries.telegram.data.db

import org.danceofvalkyries.telegram.domain.models.TelegramMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.NoPrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class TelegramNotificationMessageDbImpl(
    private val connection: Connection,
) : TelegramNotificationMessageDb {

    private companion object {
        const val TABLE_NAME = "telegram_messages"
    }

    private val idTableColumn = LongTableColumn(
        name = "id",
        primaryKey = PrimaryKey(),
    )
    private val textColumn = TextTableColumn(
        name = "text",
        primaryKey = NoPrimaryKey()
    )
    private val typeColumn = TextTableColumn(
        name = "type",
        primaryKey = NoPrimaryKey()
    )

    private val sqlQueries = TelegramMessagesSqlQueries(
        tableName = TABLE_NAME,
        idColumn = idTableColumn,
        textColumn = textColumn,
        typeColumn = typeColumn,
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
                    body = TelegramMessageBody(
                        text = textColumn.getValue(it)!!,
                        buttons = emptyList(),
                        imageUrl = null,
                        type = TelegramMessageBody.Type.valueOf(typeColumn.getValue(it)!!)
                    ),
                )
            }.toList()
    }

    private fun createTableIfNotExist(): Statement {
        return connection.createStatement()
            .also { it.execute(sqlQueries.createTableIfNotExist()) }
    }
}