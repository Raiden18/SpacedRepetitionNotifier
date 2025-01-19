package org.danceofvalkyries.utils.db.tables.columns

class IntegerTableColumn(
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
        get() = primaryKey.declare("$name INTEGER")

    override fun sqlRequestValue(value: String?): String = value ?: error("Cannot be null")
}