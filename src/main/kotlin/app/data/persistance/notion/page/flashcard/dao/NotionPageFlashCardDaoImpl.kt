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
    private val knowLevels = (1..13).associateWith { createKnowLevelColumn(it) }

    private val sqlQueries = NotionPageFlashCardSqlQueries(
        id = id,
        tableName = TABLE_NAME,
        example = example,
        answer = answer,
        imageUrl = imageUrl,
        memorizedInfo = memorizedInfo,
        notionDbId = notionDbId,
        knowLevels = knowLevels.values.toList()
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
            .map { resultSet ->
                NotionPageFlashCardDbEntity(
                    name = memorizedInfo.getValue(resultSet)!!,
                    example = example.getValue(resultSet),
                    explanation = answer.getValue(resultSet),
                    imageUrl = imageUrl.getValue(resultSet),
                    id = id.getValue(resultSet)!!,
                    notionDbId = notionDbId.getValue(resultSet)!!,
                    knowLevels = knowLevels.map { it.key to it.value.getValue(resultSet)?.toBoolean() }.toMap()
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

    private fun createKnowLevelColumn(lvl: Int): TextTableColumn {
        return TextTableColumn("know_level_$lvl")
    }
}