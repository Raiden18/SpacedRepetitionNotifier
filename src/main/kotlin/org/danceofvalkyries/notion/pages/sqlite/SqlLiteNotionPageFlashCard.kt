package org.danceofvalkyries.notion.pages.sqlite

import org.danceofvalkyries.notion.pages.NotionPageFlashCard
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

    override val knowLevels: Map<Int, Boolean>
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

    override suspend fun setKnowLevels(knowLevels: Map<Int, Boolean>) = error("Updating levels in DB is not going to be supported")
}