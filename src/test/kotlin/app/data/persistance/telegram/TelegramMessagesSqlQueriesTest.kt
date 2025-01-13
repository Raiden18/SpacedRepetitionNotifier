package app.data.persistance.telegram

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.persistance.telegram.TelegramMessageEntity
import org.danceofvalkyries.app.data.persistance.telegram.TelegramMessagesSqlQueries
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class TelegramMessagesSqlQueriesTest : FunSpec() {

    private val tableName = "table_name"
    private val idColumn = LongTableColumn("id", PrimaryKey())
    private val textColumn = TextTableColumn("text")
    private val typeColumn = TextTableColumn("type")

    private val telegramMessagesSqlQueries = TelegramMessagesSqlQueries(
        tableName,
        idColumn,
        textColumn,
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
                    text = "something",
                    type = "NOTIFICATION"
                )
            ) shouldBe "INSERT INTO $tableName (id, text, type) VALUES (12, 'something', 'NOTIFICATION');"
        }

        test("Should create table if not exist") {
            telegramMessagesSqlQueries.createTableIfNotExist() shouldBe "CREATE TABLE IF NOT EXISTS $tableName (id LONG PRIMARY KEY, text TEXT, type TEXT);"
        }

        test("Should update message by message id") {
            val message = "new message"
            val id = 228L

            telegramMessagesSqlQueries.update(
                message,
                id
            ) shouldBe "UPDATE $tableName SET text = '$message' WHERE id = $id;"
        }
    }
}