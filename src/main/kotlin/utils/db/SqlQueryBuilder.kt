package org.danceofvalkyries.utils.db

class SqlQueryBuilder {

    private val stringBuilder = mutableListOf<String>()

    fun select(what: String): SqlQueryBuilder {
        stringBuilder.add("SELECT $what")
        return this
    }

    fun from(table: String): SqlQueryBuilder {
        stringBuilder.add("FROM $table")
        return this
    }

    fun delete(): SqlQueryBuilder {
        stringBuilder.add("DELETE")
        return this
    }

    fun where(parameter: Pair<TableColumn, String>): SqlQueryBuilder {
        stringBuilder.add("WHERE ${parameter.first.name} = ${parameter.first.sqlRequestValue(parameter.second)}")
        return this
    }

    fun insert(
        into: String,
        values: List<Pair<TableColumn, String>>
    ): SqlQueryBuilder {
        val columns = values.map { it.first.name }.joinToString(", ")
        val values = values.map { it.first.sqlRequestValue(it.second) }.joinToString(", ")
        stringBuilder.add("INSERT INTO $into ($columns) VALUES ($values)")
        return this
    }

    fun createIfNotExist(
        tableName: String,
        columns: List<TableColumn>
    ): SqlQueryBuilder {
        val columnsDeclarations = columns.map { it.declaration }.joinToString(", ")
        stringBuilder.add("CREATE TABLE IF NOT EXISTS $tableName ($columnsDeclarations);")
        return this
    }

    fun update(tableName: String): SqlQueryBuilder {
        stringBuilder.add("UPDATE $tableName")
        return this
    }

    fun set(value: Pair<TableColumn, String>): SqlQueryBuilder {
        stringBuilder.add(
            "SET ${value.first.name} = ${value.first.sqlRequestValue(value.second)}"
        )
        return this
    }

    fun build(): String {
        return stringBuilder.joinToString(" ")
    }
}