package org.danceofvalkyries.app.data.sqlite.notion.pages.flashcard

import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCards
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement

class SqlLiteNotionPageFlashCards(
    private val connection: Connection
) : NotionPageFlashCards {

    companion object {
        private const val TABLE_NAME = "flash_cards_to_revise"
    }

    private val idColumn = TextTableColumn(
        name = "id",
        primaryKey = PrimaryKey(),
    )
    private val exampleColumn = TextTableColumn("example")
    private val answerColumn = TextTableColumn("answer")
    private val imageUrlColumn = TextTableColumn("image_url")
    private val nameColumn = TextTableColumn("name")
    private val notionDbIdColumn = TextTableColumn("notion_db_id")
    private val knowLevelsColumns = (1..13).associateWith { createKnowLevelColumn(it) }

    override fun iterate(): Sequence<NotionPageFlashCard> {
        return createStatement().let {
            it.executeQuery(
                SqlQuery {
                    select(idColumn)
                    from(TABLE_NAME)
                }
            )
        }.asSequence()
            .map {
                SqlLiteNotionPageFlashCard(
                    id = idColumn.getValue(it)!!,
                    tableName = TABLE_NAME,
                    idColumn = idColumn,
                    nameColumn = nameColumn,
                    exampleColumn = exampleColumn,
                    answerColumn = answerColumn,
                    imageUrlColumn = imageUrlColumn,
                    notionDbIdColumn = notionDbIdColumn,
                    knowLevelsColumns = knowLevelsColumns,
                    connection = connection
                )
            }
    }

    override fun add(
        id: String,
        coverUrl: String?,
        notionDbId: String,
        name: String,
        example: String?,
        explanation: String?,
        knowLevels: Map<Int, Boolean>
    ): NotionPageFlashCard {
        val knowLevelDbValues = knowLevels
            .keys
            .map { level ->
                val knowLevelColumn = knowLevelsColumns.values.toList().first { it.name.endsWith(level.toString()) }
                knowLevelColumn to knowLevels[level]?.toString()
            }
        createStatement().execute(
            SqlQuery {
                insert(
                    into = TABLE_NAME,
                    values = listOf(
                        idColumn to id,
                        nameColumn to name,
                        exampleColumn to example,
                        imageUrlColumn to coverUrl,
                        notionDbIdColumn to notionDbId,
                        answerColumn to explanation,
                    ) + knowLevelDbValues
                )
            }
        )
        return SqlLiteNotionPageFlashCard(
            id = id,
            tableName = TABLE_NAME,
            idColumn = idColumn,
            nameColumn = nameColumn,
            exampleColumn = exampleColumn,
            answerColumn = answerColumn,
            imageUrlColumn = imageUrlColumn,
            notionDbIdColumn = notionDbIdColumn,
            knowLevelsColumns = knowLevelsColumns,
            connection = connection
        )
    }

    override fun delete(id: String) {
        createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        delete()
                        from(TABLE_NAME)
                        where(idColumn to id)
                    }
                )
            }

    }

    override fun clear() {
        createStatement().execute(
            SqlQuery {
                delete()
                from(TABLE_NAME)
            }
        )
    }

    private fun createStatement(): Statement {
        return connection.createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        createIfNotExist(
                            tableName = TABLE_NAME,
                            columns = listOf(
                                idColumn,
                                exampleColumn,
                                answerColumn,
                                imageUrlColumn,
                                nameColumn,
                                notionDbIdColumn,
                            ) + knowLevelsColumns.values
                        )
                    }
                )
            }
    }

    private fun createKnowLevelColumn(lvl: Int): TextTableColumn {
        return TextTableColumn("know_level_$lvl")
    }
}