package org.danceofvalkyries.notion.data.repositories.db

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.utils.db.SqlQueryBuilder
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
        return SqlQueryBuilder()
            .select("*")
            .from(tableName)
            .where(notionDbId to databaseId)
            .build()
    }

    fun insert(
        flashCard: FlashCard
    ): String {
        return SqlQueryBuilder()
            .insert(
                into = tableName,
                values = listOf(
                    id to flashCard.metaInfo.id,
                    example to flashCard.example,
                    answer to flashCard.answer,
                    imageUrl to flashCard.imageUrl?.url,
                    memorizedInfo to flashCard.memorizedInfo,
                    notionDbId to flashCard.metaInfo.parentDbId,
                )
            ).build()
    }

    fun createTableIfNotExisted(): String {
        return SqlQueryBuilder()
            .createIfNotExist(
                tableName = tableName,
                columns = listOf(id, example, answer, imageUrl, memorizedInfo, notionDbId)
            )
            .build()
    }

    fun delete(id: String): String {
        return SqlQueryBuilder()
            .delete()
            .from(tableName)
            .where(this.id to id)
            .build()
    }

    fun deleteAll(): String {
        return SqlQueryBuilder()
            .delete()
            .from(tableName)
            .build()
    }
}