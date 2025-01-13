package org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao

import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class NotionPageFlashCardDaoImpl(
    private val connection: Connection,
) : NotionPageFlashCardDao {

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
    private val memorizedInfo = TextTableColumn("name")
    private val notionDbId = TextTableColumn("notion_db_id")

    private val sqlQueries = NotionPageFlashCardSqlQueries(
        tableName = TABLE_NAME,
        example = example,
        answer = answer,
        imageUrl = imageUrl,
        memorizedInfo = memorizedInfo,
        notionDbId = notionDbId,
        id = id,
    )

    override suspend fun insert(entity: NotionPageFlashCardDbEntity) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.insert(entity)) }
            .also { it.close() }
    }

    override suspend fun getAllFor(notionDataBaseId: String): List<NotionPageFlashCardDbEntity> {
        return createTableIfNotExist()
            .executeQuery(sqlQueries.selectAll(notionDataBaseId))
            .asSequence()
            .map {
                NotionPageFlashCardDbEntity(
                    name = memorizedInfo.getValue(it)!!,
                    example = example.getValue(it),
                    explanation = answer.getValue(it),
                    imageUrl = imageUrl.getValue(it),
                    id = id.getValue(it)!!,
                    notionDbId = notionDbId.getValue(it)!!
                )
            }.toList()
    }

    override suspend fun delete(notionPageId: String) {
        createTableIfNotExist()
            .also { it.execute(sqlQueries.delete(notionPageId)) }
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