package notion.data.repositories.db.table

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.data.repositories.db.table.FlashCardsTablesSqlQueries
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.NotionDbId
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class FlashCardsTablesSqlQueriesTest : FunSpec() {

    private val id = TextTableColumn(
        "id",
        PrimaryKey()
    )
    private val name = TextTableColumn("name")
    private val tableName = "tables"

    private val flashCardsTablesSqlQueries = FlashCardsTablesSqlQueries(
        tableName = tableName,
        id = id,
        name = name,
    )

    private val notionDataBase = NotionDataBase(
        id = NotionDbId("228"),
        name = "English Grammar",
    )

    init {
        test("Should create query for insert") {
            flashCardsTablesSqlQueries.insert(
                notionDataBase
            ) shouldBe "INSERT INTO $tableName (id, name) VALUES ('228', 'English Grammar');"
        }

        test("Should create Query to clear table") {
            flashCardsTablesSqlQueries
                .clear() shouldBe "DELETE FROM $tableName;"
        }

        test("Should create query to CREATE IF NOT EXIST") {
            flashCardsTablesSqlQueries
                .createIfNotExist() shouldBe "CREATE TABLE IF NOT EXISTS $tableName (id TEXT PRIMARY KEY, name TEXT);"
        }

        test("Should create query for Select all") {
            flashCardsTablesSqlQueries
                .selectAll() shouldBe "SELECT * FROM $tableName;"
        }
    }
}