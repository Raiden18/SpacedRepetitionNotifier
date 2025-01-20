package org.danceofvalkyries.notion.pages.sqlite

import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn
import java.sql.Connection

class SqlLiteNotionPageFlashCard(
    private val id: String,
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

    override suspend fun getId(): String {
        return id
    }

    override suspend fun getCoverUrl(): String? {
        return connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(imageUrlColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(imageUrlColumn::getValue)
    }

    override suspend fun getNotionDbId(): String {
        return connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(notionDbIdColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(notionDbIdColumn::getValue)!!
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

    override suspend fun getExample(): String? {
        return connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(exampleColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(exampleColumn::getValue)
    }

    override suspend fun getExplanation(): String? {
        return connection.createStatement()
            .executeQuery(
                SqlQuery {
                    select(answerColumn)
                    from(tableName)
                    where(idColumn to id)
                }
            )?.let(answerColumn::getValue)
    }

    override suspend fun getKnowLevels(): Map<Int, Boolean> {
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