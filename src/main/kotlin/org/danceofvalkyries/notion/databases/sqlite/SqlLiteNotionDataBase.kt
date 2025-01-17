package org.danceofvalkyries.notion.databases.sqlite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.notion.pages.sqlite.SqlLiteNotionPageFlashCard
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asFlow
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement
import java.util.*

class SqlLiteNotionDataBase(
    private val id: String,
    private val tableName: String,
    private val idColumn: TextTableColumn,
    private val nameColumn: TextTableColumn,
    private val connection: Connection,
) : NotionDataBase {

    private val pageIdColumn = TextTableColumn(
        name = "page_id",
        primaryKey = PrimaryKey(),
    )
    private val pageExampleColumn = TextTableColumn("example")
    private val pageAnswerColumn = TextTableColumn("answer")
    private val pageImageUrlColumn = TextTableColumn("image_url")
    private val pageNameColumn = TextTableColumn("name")
    private val pageNotionDbIdColumn = TextTableColumn(idColumn.name)
    private val pageKnowLevelsColumns = (1..13).associateWith {
        createKnowLevelColumn(it)
    }

    override fun getId(): String {
        return id
    }

    override suspend fun getName(): String {
        return connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(nameColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(nameColumn::getValue)!!
    }

    override suspend fun iterate(): Flow<NotionPageFlashCard> {
        return createStatement().let {
            it.executeQuery(
                SqlQuery {
                    select(pageIdColumn)
                    from(getPageTableName())
                }
            )
        }.asFlow()
            .map {
                SqlLiteNotionPageFlashCard(
                    id = pageIdColumn.getValue(it)!!,
                    tableName = getPageTableName(),
                    idColumn = pageIdColumn,
                    nameColumn = pageNameColumn,
                    exampleColumn = pageExampleColumn,
                    answerColumn = pageAnswerColumn,
                    imageUrlColumn = pageImageUrlColumn,
                    notionDbIdColumn = pageNotionDbIdColumn,
                    knowLevelsColumns = pageKnowLevelsColumns,
                    connection = connection
                )
            }
            .filter { it.notionDbID == id }
    }

    override suspend fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        val id = notionPageFlashCard.id
        val coverUrl = notionPageFlashCard.coverUrl
        val name = notionPageFlashCard.name
        val example = notionPageFlashCard.example
        val explanation = notionPageFlashCard.explanation
        val knowLevels = notionPageFlashCard.knowLevels
        val knowLevelDbValues = knowLevels
            .keys
            .map { level ->
                val knowLevelColumn = pageKnowLevelsColumns.values.toList().first { it.name.endsWith(level.toString()) }
                knowLevelColumn to knowLevels[level]?.toString()
            }
        createStatement().execute(
            SqlQuery {
                insert(
                    into = getPageTableName(),
                    values = listOf(
                        pageIdColumn to id,
                        pageNameColumn to name,
                        pageExampleColumn to example,
                        pageImageUrlColumn to coverUrl,
                        pageNotionDbIdColumn to this@SqlLiteNotionDataBase.id,
                        pageAnswerColumn to explanation,
                    ) + knowLevelDbValues
                )
            }
        )
        return getPageBy(id)
    }

    override suspend fun getPageBy(pageId: String): NotionPageFlashCard {
        return SqlLiteNotionPageFlashCard(
            id = id,
            tableName = getPageTableName(),
            idColumn = pageIdColumn,
            nameColumn = pageNameColumn,
            exampleColumn = pageExampleColumn,
            answerColumn = pageAnswerColumn,
            imageUrlColumn = pageImageUrlColumn,
            notionDbIdColumn = pageNotionDbIdColumn,
            knowLevelsColumns = pageKnowLevelsColumns,
            connection = connection
        )
    }

    override suspend fun clear() {
        createStatement().execute(
            SqlQuery {
                delete()
                from(getPageTableName())
            }
        )
    }

    override suspend fun delete(pageId: String) {
        createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        delete()
                        from(getPageTableName())
                        where(pageIdColumn to pageId)
                    }
                )
            }

    }

    private fun createKnowLevelColumn(lvl: Int): TextTableColumn {
        return TextTableColumn("know_level_$lvl")
    }

    private suspend fun createStatement(): Statement {
        return connection.createStatement()
            .also {
                it.execute(
                    SqlQuery {
                        createIfNotExist(
                            tableName = getPageTableName(),
                            columns = listOf(
                                pageIdColumn,
                                pageExampleColumn,
                                pageAnswerColumn,
                                pageImageUrlColumn,
                                pageNameColumn,
                                pageNotionDbIdColumn,
                            ) + pageKnowLevelsColumns.values
                        )
                    }
                )
            }
    }


    private suspend fun getPageTableName(): String {
        return "flash_cards_to_revise_${getName().lowercase(Locale.getDefault()).replace(" ", "_")} "
    }
}