package org.danceofvalkyries.notion.data.repositories.db.flashcards

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TableColumn
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class FlashCardSqlQueries(
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

    fun insert(
        flashCard: FlashCard
    ): String {
        return SqlQuery {
            insert(
                into = tableName,
                values = listOf(
                    id to flashCard.metaInfo.id,
                    example to flashCard.example,
                    answer to flashCard.answer,
                    imageUrl to flashCard.imageUrl?.url,
                    memorizedInfo to flashCard.memorizedInfo,
                    notionDbId to flashCard.metaInfo.notionDbId.valueId,
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
            where(this@FlashCardSqlQueries.id to id)
        }
    }

    fun deleteAll(): String {
        return SqlQuery {
            delete()
            from(tableName)
        }
    }
}