package app.data.persistance.telegram.dao

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessageEntity
import org.danceofvalkyries.app.data.persistance.telegram.messages.dao.TelegramMessagesSqlQueries
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class TelegramMessagesSqlQueriesTest : FunSpec() {

    private val tableName = "table_name"
    private val idColumn = LongTableColumn("id", PrimaryKey())
    private val typeColumn = TextTableColumn("type")

    private val telegramMessagesSqlQueries = TelegramMessagesSqlQueries(
        tableName,
        idColumn,
        typeColumn,
    )

    init {
        test("Should build Delete request") {
            telegramMessagesSqlQueries.delete(
                id = 228
            ) shouldBe "DELETE FROM $tableName WHERE id = 228;"
        }

        test("Should build select request") {
            telegramMessagesSqlQueries.getAll() shouldBe "SELECT * FROM $tableName;"
        }

        test("Should insert into table") {
            telegramMessagesSqlQueries.insert(
                TelegramMessageEntity(
                    id = 12,
                    type = "NOTIFICATION"
                )
            ) shouldBe "INSERT INTO $tableName (id, type) VALUES (12, 'NOTIFICATION');"
        }

        test("Should create table if not exist") {
            telegramMessagesSqlQueries.createTableIfNotExist() shouldBe "CREATE TABLE IF NOT EXISTS $tableName (id LONG PRIMARY KEY, type TEXT);"
        }

        test("Query to get Message by id") {
            telegramMessagesSqlQueries.selectBy(228) shouldBe "SELECT * FROM $tableName WHERE id = 228;"
        }
    }
}