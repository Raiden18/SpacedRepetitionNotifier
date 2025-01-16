package app.data.sqlite.notion.databases

import app.domain.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.sqlite.notion.pages.flashcard.SqlLiteNotionPageFlashCard
import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.asSequence
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection
import java.sql.Statement
import java.util.*

class SqlLiteNotionDataBase(
    override val id: String,
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
    private val pageKnowLevelsColumns = (1..13).associateWith { createKnowLevelColumn(it) }

    override val name: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(nameColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(nameColumn::getValue)!!


    override fun iterate(): Sequence<NotionPageFlashCard> {
        return createStatement().let {
            it.executeQuery(
                SqlQuery {
                    select(pageIdColumn)
                    from(getPageTableName())
                }
            )
        }.asSequence()
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

    override fun add(
        id: String,
        coverUrl: String?,
        name: String,
        example: String?,
        explanation: String?,
        knowLevels: Map<Int, Boolean>
    ): NotionPageFlashCard {
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

    override fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        return add(
            id = notionPageFlashCard.id,
            coverUrl = notionPageFlashCard.coverUrl,
            name = notionPageFlashCard.name,
            explanation = notionPageFlashCard.explanation,
            example = notionPageFlashCard.example,
            knowLevels = notionPageFlashCard.knowLevels.levels
        )
    }

    override fun update(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        error("Updating page in local DB is not supported")
    }

    override fun clear() {
        createStatement().execute(
            SqlQuery {
                delete()
                from(getPageTableName())
            }
        )
    }

    override fun delete(pageId: String) {
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

    private fun createStatement(): Statement {
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


    private fun getPageTableName(): String {
        return "flash_cards_to_revise_${name.lowercase(Locale.getDefault()).replace(" ", "_")} "
    }
}