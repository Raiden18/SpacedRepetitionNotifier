package org.danceofvalkyries.utils.db.tables.columns

import java.sql.ResultSet

class BoolenTableColumn(
    override val name: String,
    override val primaryKey: PrimaryKey,
) : TableColumn {

    constructor(name: String) : this(name, NoPrimaryKey())

    override val declaration: String
        get() = primaryKey.declare("$name BOOLEAN")

    fun getValue(resultSet: ResultSet): Boolean? = resultSet.getBoolean(name)

    override fun sqlRequestValue(value: String?): String {
        return if (value == null) {
            "NULL"
        } else {
            "$value"
        }
    }
}