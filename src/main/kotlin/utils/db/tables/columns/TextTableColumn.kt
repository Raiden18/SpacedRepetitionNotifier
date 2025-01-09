package org.danceofvalkyries.utils.db.tables.columns

import java.sql.ResultSet

data class TextTableColumn(
    override val name: String,
    override val primaryKey: PrimaryKey,
) : TableColumn {

    override val declaration: String
        get() = primaryKey.declare("$name TEXT")

    fun getValue(resultSet: ResultSet): String = resultSet.getString(name)

    override fun sqlRequestValue(value: String): String {
        return "'$value'"
    }
}