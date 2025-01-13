package org.danceofvalkyries.app.data.persistance.telegram

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

    override suspend fun save(entity: TelegramMessageEntity) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(entity)) }
            .also { it.close() }
    }

    override suspend fun delete(entity: TelegramMessageEntity) {
        connection.createStatement()
            .also { it.execute(sqlQueries.delete(entity.id)) }
            .also { it.close() }
    }

    override suspend fun update(entity: TelegramMessageEntity, messageId: Long) {
        connection.createStatement()
            .also { it.execute(sqlQueries.update(entity.text, messageId)) }
            .also { it.close() }
    }

    override suspend fun getAll(): List<TelegramMessageEntity> {
        return createTableIfNotExist()
            .executeQuery(sqlQueries.getAll())
            .asSequence()
            .map {
                TelegramMessageEntity(
                    id = idTableColumn.getValue(it),
                    text = textColumn.getValue(it)!!,
                    type = typeColumn.getValue(it)!!,
                )
            }.toList()
    }

    private fun createTableIfNotExist(): Statement {
        return connection.createStatement()
            .also { it.execute(sqlQueries.createTableIfNotExist()) }
    }
}