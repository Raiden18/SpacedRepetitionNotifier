package org.danceofvalkyries.utils.db

import java.sql.ResultSet

interface TableColumn {

    val name: String
    val isPrimaryKey: Boolean
    val declaration: String

    fun sqlRequestValue(value: String): String

    data class Long(
        override val name: String,
        override val isPrimaryKey: Boolean,
    ) : TableColumn {
        fun getValue(resultSet: ResultSet) = resultSet.getLong(name)

        override val declaration: String
            get() {
                val strings = mutableListOf("$name LONG")
                if (isPrimaryKey) {
                    strings.add("PRIMARY KEY")
                }
                return strings.joinToString(" ")
            }

        override fun sqlRequestValue(value: String): String = value
    }

    data class Text(
        override val name: String,
        override val isPrimaryKey: Boolean,
    ) : TableColumn {
        fun getValue(resultSet: ResultSet): String = resultSet.getString(name)

        override val declaration: String
            get() {
                val strings = mutableListOf("$name TEXT")
                if (isPrimaryKey) {
                    strings.add("PRIMARY_KEY")
                }
                return strings.joinToString(" ")
            }

        override fun sqlRequestValue(value: String): String {
            return "'$value'"
        }
    }
}
