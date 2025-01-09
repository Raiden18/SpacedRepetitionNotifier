package org.danceofvalkyries.utils.db.tables.columns

interface TableColumn {

    val name: String
    val primaryKey: PrimaryKey
    val declaration: String

    fun sqlRequestValue(value: String): String
}
