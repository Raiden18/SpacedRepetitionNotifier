package org.danceofvalkyries.app.data.persistance.notion.database

import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseDao
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseEntity
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId

class NotionDatabaseDataBaseTableImpl(
    private val notionDataBaseDao: NotionDataBaseDao
) : NotionDatabaseDataBaseTable {

    override suspend fun insert(notionDataBases: List<NotionDataBase>) {
        notionDataBases.map {
            NotionDataBaseEntity(
                id = it.id.rawValue,
                name = it.name
            )
        }.forEach { notionDataBaseDao.insert(it) }
    }

    override suspend fun getAll(): List<NotionDataBase> {
        return notionDataBaseDao.getAll()
            .map {
                NotionDataBase(
                    id = NotionId(it.id),
                    name = it.name,
                )
            }
    }

    override suspend fun clear() {
        notionDataBaseDao.clear()
    }
}