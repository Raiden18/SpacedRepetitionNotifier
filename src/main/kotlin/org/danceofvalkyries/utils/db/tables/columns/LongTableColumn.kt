package org.danceofvalkyries.utils.db.tables.columns

import java.sql.ResultSet

data class LongTableColumn(
    override val name: String,
    override val primaryKey: PrimaryKey,
) : TableColumn {

    override val declaration: String
        get() = primaryKey.declare("$name LONG")

    fun getValue(resultSet: ResultSet) = resultSet.getLong(name)

    override fun sqlRequestValue(value: String?): String = value ?: error("Cannot be null")
}
