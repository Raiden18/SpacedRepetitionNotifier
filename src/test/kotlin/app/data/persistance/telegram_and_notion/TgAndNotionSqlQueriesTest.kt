package app.data.persistance.telegram_and_notion

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.persistance.telegram_and_notion.TgAndNotionSqlQueries
import org.danceofvalkyries.utils.db.tables.columns.LongTableColumn
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class TgAndNotionSqlQueriesTest : FunSpec() {

    private val notionIdColumn = TextTableColumn("notion_page_id")
    private val telegramMessageId = LongTableColumn("telegram_message_id", PrimaryKey())
    private val tableName = "table_name"

    private val tgAndNotionSqlQueries = TgAndNotionSqlQueries(
        tableName,
        notionIdColumn,
        telegramMessageId,
    )

    init {
        test("Should create table if not exist") {
            tgAndNotionSqlQueries.createTableIfNotExist() shouldBe "CREATE TABLE IF NOT EXISTS $tableName (telegram_message_id LONG PRIMARY KEY, notion_page_id TEXT);"
        }

        test("Should create insert statement") {
            tgAndNotionSqlQueries.insert(
                228,
                "322",
            ) shouldBe "INSERT INTO $tableName (telegram_message_id, notion_page_id) VALUES (228, '322');"
        }

        test("Should create Select query by messageId") {
            tgAndNotionSqlQueries.selectBy(228L) shouldBe "SELECT * FROM $tableName WHERE telegram_message_id = 228;"
        }

        test("Should create Select query by notionId") {
            tgAndNotionSqlQueries.selectBy("229") shouldBe "SELECT * FROM $tableName WHERE notion_page_id = '229';"
        }

        test("Should create Delete Request by messageId"){
            tgAndNotionSqlQueries.deleteBy(228L) shouldBe "DELETE FROM $tableName WHERE telegram_message_id = 228;"
        }
    }
}