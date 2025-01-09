package org.danceofvalkyries.utils.db.tables.columns

interface PrimaryKey {
    fun declare(value: String): String
}

fun PrimaryKey(): PrimaryKey {
    return object : PrimaryKey {
        override fun declare(value: String): String = "$value PRIMARY KEY"
    }
}

fun NoPrimaryKey(): PrimaryKey {
    return object : PrimaryKey {
        override fun declare(value: String): String = value
    }
}
