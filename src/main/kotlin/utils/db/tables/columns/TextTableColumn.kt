package org.danceofvalkyries.utils.db.tables.columns

import java.sql.ResultSet

data class TextTableColumn(
    override val name: String,
    override val primaryKey: PrimaryKey,
) : TableColumn {

    constructor(
        name: String,
    ) : this(
        name,
        NoPrimaryKey(),
    )

    override val declaration: String
        get() = primaryKey.declare("$name TEXT")

    fun getValue(resultSet: ResultSet): String? = resultSet.getString(name)

    override fun sqlRequestValue(value: String?): String {
        return if (value == null) {
            "NULL"
        } else {
            "'${value.replace("'", "''")}'"
        }
    }
}