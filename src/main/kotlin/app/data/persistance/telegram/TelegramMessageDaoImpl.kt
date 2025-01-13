package org.danceofvalkyries.app.data.persistance.telegram

import org.danceofvalkyries.telegram.api.models.TelegramMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.NoPrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class TelegramMessageDaoImpl(
    private val connection: Connection,
) : TelegramMessageDao {

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
        val entity = TelegramMessageEntity(
            id = telegramMessage.id,
            text = telegramMessage.body.text.get(),
            type = telegramMessage.body.type.toString()
        )
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(entity)) }
            .also { it.close() }
    }

    override suspend fun delete(oldTelegramMessage: TelegramMessage) {
        connection.createStatement()
            .also { it.execute(sqlQueries.delete(oldTelegramMessage.id)) }
            .also { it.close() }
    }

    override suspend fun update(text: TelegramMessageBody, messageId: Long) {
        connection.createStatement()
            .also { it.execute(sqlQueries.update(text.text.get(), messageId)) }
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
                        telegramButtons = emptyList(),
                        telegramImageUrl = null,
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