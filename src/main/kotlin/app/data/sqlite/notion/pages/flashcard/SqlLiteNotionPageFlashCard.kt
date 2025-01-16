package org.danceofvalkyries.app.data.sqlite.notion.pages.flashcard

import org.danceofvalkyries.app.domain.notion.pages.flashcard.NotionPageFlashCard
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteNotionPageFlashCard(
    override val id: String,
    private val connection: Connection,
    private val tableName: String,
    private val nameColumn: TextTableColumn,
    private val idColumn: TextTableColumn,
    private val exampleColumn: TextTableColumn,
    private val answerColumn: TextTableColumn,
    private val imageUrlColumn: TextTableColumn,
    private val notionDbIdColumn: TextTableColumn,
    private val knowLevelsColumns: Map<Int, TextTableColumn>,
) : NotionPageFlashCard {

    override val coverUrl: String?
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(imageUrlColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(imageUrlColumn::getValue)

    override val notionDbID: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(notionDbIdColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(notionDbIdColumn::getValue)!!

    override val name: String
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(nameColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(nameColumn::getValue)!!

    override val example: String?
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(exampleColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(exampleColumn::getValue)

    override val explanation: String?
        get() = connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(answerColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(answerColumn::getValue)

    override val knowLevels: NotionPageFlashCard.KnowLevels
        get() = object : NotionPageFlashCard.KnowLevels {
            override val levels: Map<Int, Boolean>
                get() {
                    val sqlQuery = SqlQuery {
                        select(knowLevelsColumns.values)
                        from(tableName)
                        where(idColumn to id)
                    }
                    return connection.createStatement()
                        .executeQuery(sqlQuery).let { resultSet ->
                            knowLevelsColumns.map { it.key to it.value.getValue(resultSet)?.toBoolean() }.toMap()
                                .filterValues { it != null }
                                .mapValues { it.value!! }
                        }
                }
        }

    override fun setKnowLevels(knowLevels: NotionPageFlashCard.KnowLevels) {
        error("Now supported")
    }

    /*override fun setKnowLevels(knowLevels: NotionPageFlashCard.KnowLevels) {
        val sqlQuery = SqlQuery {
            update(tableName)
            knowLevels.levels
            set(
                values = knowLevels.levels.map { (lvl, vl) ->
                    val column = knowLevelsColumns[lvl]!!
                    column to vl.toString()
                }.toList()
            )
        }
        connection.createStatement().execute(sqlQuery)
    }*/
}