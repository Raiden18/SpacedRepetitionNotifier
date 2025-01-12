package org.danceofvalkyries.notion.data.repositories.db.flashcards

import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class FlashCardDbTableImpl(
    private val connection: Connection,
) : FlashCardDbTable {

    companion object {
        private const val TABLE_NAME = "flash_cards_to_revise"
    }

    private val id = TextTableColumn(
        name = "id",
        primaryKey = PrimaryKey(),
    )
    private val example = TextTableColumn("example")
    private val answer = TextTableColumn("answer")
    private val imageUrl = TextTableColumn("image_url")
    private val memorizedInfo = TextTableColumn("memorized_info")
    private val notionDbId = TextTableColumn("notion_db_id")

    private val sqlQueries = FlashCardSqlQueries(
        tableName = TABLE_NAME,
        example = example,
        answer = answer,
        imageUrl = imageUrl,
        memorizedInfo = memorizedInfo,
        notionDbId = notionDbId,
        id = id,
    )

    override suspend fun insert(entity: FlashCardDbEntity) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(entity)) }
            .also { it.close() }
    }

    override suspend fun getAllFor(notionDataBaseId: String): List<FlashCardDbEntity> {
        return createTableIfNotExist()
            .executeQuery(sqlQueries.selectAll(notionDataBaseId))
            .asSequence()
            .map {
                FlashCardDbEntity(
                    memorizedInfo = memorizedInfo.getValue(it)!!,
                    example = example.getValue(it),
                    answer = answer.getValue(it),
                    imageUrl = imageUrl.getValue(it),
                    cardId = id.getValue(it)!!,
                    notionDbId = notionDbId.getValue(it)!!
                )
            }.toList()
    }

    override suspend fun delete(entity: FlashCardDbEntity) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.delete(entity.cardId)) }
            .also { it.close() }
    }

    override suspend fun clear() {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.deleteAll()) }
            .also { it.close() }
    }

    private fun createTableIfNotExist(): Statement {
        return connection.createStatement()
            .also { it.execute(sqlQueries.createTableIfNotExisted()) }
    }
}