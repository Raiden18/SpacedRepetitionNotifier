package app.data.persistance.notion.database.dao

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDataBaseEntity
import org.danceofvalkyries.app.data.persistance.notion.database.dao.NotionDbSqlQueries
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class NotionTablesSqlQueriesTest : FunSpec() {

    private val id = TextTableColumn(
        "id",
        PrimaryKey()
    )
    private val name = TextTableColumn("name")
    private val tableName = "tables"

    private val notionDbSqlQueries = NotionDbSqlQueries(
        tableName = tableName,
        id = id,
        name = name,
    )

    private val notionDataBaseEntity = NotionDataBaseEntity(
        id = "228",
        name = "English Grammar",
    )

    init {
        test("Should create query for insert") {
            notionDbSqlQueries.insert(notionDataBaseEntity) shouldBe "INSERT INTO $tableName (id, name) VALUES ('228', 'English Grammar');"
        }

        test("Should create Query to clear table") {
            notionDbSqlQueries.clear() shouldBe "DELETE FROM $tableName;"
        }

        test("Should create query to CREATE IF NOT EXIST") {
            notionDbSqlQueries.createIfNotExist() shouldBe "CREATE TABLE IF NOT EXISTS $tableName (id TEXT PRIMARY KEY, name TEXT);"
        }

        test("Should create query for Select all") {
            notionDbSqlQueries.selectAll() shouldBe "SELECT * FROM $tableName;"
        }
    }
}