package org.danceofvalkyries.utils.db.tables.columns

interface PrimaryKey {
    fun declare(value: String): String
}

fun PrimaryKey(autoIncrement: AutoIncrement = NoAutoIncrement()): PrimaryKey {
    return object : PrimaryKey {
        override fun declare(value: String): String {
            return autoIncrement.declare("$value PRIMARY KEY")
        }
    }
}

fun NoPrimaryKey(): PrimaryKey {
    return object : PrimaryKey {
        override fun declare(value: String): String = value
    }
}
