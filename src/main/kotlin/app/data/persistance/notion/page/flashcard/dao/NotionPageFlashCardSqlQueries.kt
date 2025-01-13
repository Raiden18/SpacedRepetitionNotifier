package org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao

import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TableColumn
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class NotionPageFlashCardSqlQueries(
    private val tableName: String,
    private val id: TextTableColumn,
    private val example: TextTableColumn,
    private val answer: TextTableColumn,
    private val imageUrl: TextTableColumn,
    private val memorizedInfo: TextTableColumn,
    private val notionDbId: TableColumn,
) {

    fun selectAll(databaseId: String): String {
        return SqlQuery {
            select("*")
            from(tableName)
            where(notionDbId to databaseId)
        }
    }

    fun insert(notionPageFlashCardDbEntity: NotionPageFlashCardDbEntity): String {
        return SqlQuery {
            insert(
                into = tableName,
                values = listOf(
                    id to notionPageFlashCardDbEntity.id,
                    example to notionPageFlashCardDbEntity.example,
                    answer to notionPageFlashCardDbEntity.explanation,
                    imageUrl to notionPageFlashCardDbEntity.imageUrl,
                    memorizedInfo to notionPageFlashCardDbEntity.name,
                    notionDbId to notionPageFlashCardDbEntity.notionDbId,
                )
            )
        }
    }

    fun createTableIfNotExisted(): String {
        return SqlQuery {
            createIfNotExist(
                tableName = tableName,
                columns = listOf(id, example, answer, imageUrl, memorizedInfo, notionDbId)
            )
        }
    }

    fun delete(id: String): String {
        return SqlQuery {
            delete()
            from(tableName)
            where(this@NotionPageFlashCardSqlQueries.id to id)
        }
    }

    fun deleteAll(): String {
        return SqlQuery {
            delete()
            from(tableName)
        }
    }
}