package org.danceofvalkyries.utils.db.tables.columns

interface AutoIncrement {
    fun declare(value: String): String
}

fun AutoIncrement(): AutoIncrement {
    return object : AutoIncrement {
        override fun declare(value: String): String = "$value AUTOINCREMENT"
    }
}

fun NoAutoIncrement(): AutoIncrement {
    return object : AutoIncrement {
        override fun declare(value: String): String = value
    }
}