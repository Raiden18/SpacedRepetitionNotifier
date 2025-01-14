package org.danceofvalkyries.app.data.persistance.telegram.messages.dao

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

    private val typeColumn = TextTableColumn(
        name = "type",
        primaryKey = NoPrimaryKey()
    )

    private val sqlQueries = TelegramMessagesSqlQueries(
        tableName = TABLE_NAME,
        idColumn = idTableColumn,
        typeColumn = typeColumn,
    )

    override suspend fun save(entity: TelegramMessageEntity) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(entity)) }
            .also { it.close() }
    }

    override suspend fun delete(id: Long) {
        connection.createStatement()
            .also { it.execute(sqlQueries.delete(id)) }
            .also { it.close() }
    }

    override suspend fun update(entity: TelegramMessageEntity, messageId: Long) = Unit

    override suspend fun getAll(): List<TelegramMessageEntity> {
        return createTableIfNotExist()
            .executeQuery(sqlQueries.getAll())
            .asSequence()
            .map {
                TelegramMessageEntity(
                    it,
                    idTableColumn,
                    typeColumn,
                )
            }.toList()
    }

    override suspend fun getBy(id: Long): TelegramMessageEntity? {
        return createTableIfNotExist()
            .executeQuery(sqlQueries.selectBy(id))
            .asSequence()
            .map {
                TelegramMessageEntity(
                    it,
                    idTableColumn,
                    typeColumn,
                )
            }.toList()
            .firstOrNull()
    }

    private fun createTableIfNotExist(): Statement {
        return connection.createStatement()
            .also { it.execute(sqlQueries.createTableIfNotExist()) }
    }
}