package app.data.persistance.notion.page.dao.flashcard

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardDbEntity
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.NotionPageFlashCardSqlQueries
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class NotionPageFlashCardSqlQueriesTest : FunSpec() {
    private val tableName = "flash_cards"

    private val id = TextTableColumn(
        name = "id",
        primaryKey = PrimaryKey(),
    )

    private val example = TextTableColumn("example")
    private val explanation = TextTableColumn("explanation")
    private val imageUrl = TextTableColumn("image_url")
    private val name = TextTableColumn("name")
    private val notionDbId = TextTableColumn("notion_db_id")

    private val flashcard = NotionPageFlashCardDbEntity(
        name = "1",
        example = "2",
        explanation = "3",
        imageUrl = "4",
        id = "5",
        notionDbId = "6",
    )

    private lateinit var notionPageFlashCardSqlQueries: NotionPageFlashCardSqlQueries

    init {
        beforeTest {
            notionPageFlashCardSqlQueries = NotionPageFlashCardSqlQueries(
                tableName,
                id,
                example,
                explanation,
                imageUrl,
                name,
                notionDbId,
            )
        }

        test("Should create if not exist table query") {
            notionPageFlashCardSqlQueries
                .createTableIfNotExisted() shouldBe "CREATE TABLE IF NOT EXISTS $tableName (id TEXT PRIMARY KEY, example TEXT, explanation TEXT, image_url TEXT, name TEXT, notion_db_id TEXT);"
        }

        test("Should create select query for flashcards from specific notion db") {
            val dbId = "228"
            notionPageFlashCardSqlQueries
                .selectAll(dbId) shouldBe "SELECT * FROM $tableName WHERE notion_db_id = '$dbId';"
        }

        test("Should create insert statement") {
            notionPageFlashCardSqlQueries
                .insert(flashcard) shouldBe "INSERT INTO $tableName (id, example, explanation, image_url, name, notion_db_id) VALUES ('5', '2', '3', '4', '1', '6');"
        }

        test("Should create insert statements with NULL") {
            val flashcard = NotionPageFlashCardDbEntity(
                name = "1",
                example = null,
                explanation = null,
                imageUrl = null,
                id = "5",
                notionDbId = "6",
            )

            notionPageFlashCardSqlQueries
                .insert(
                    flashcard
                ) shouldBe "INSERT INTO $tableName (id, example, explanation, image_url, name, notion_db_id) VALUES ('5', NULL, NULL, NULL, '1', '6');"
        }

        test("Should create delete statement") {
            notionPageFlashCardSqlQueries
                .delete(
                    flashcard.id
                ) shouldBe "DELETE FROM $tableName WHERE id = '5';"
        }

        test("Should create delete all statements") {
            notionPageFlashCardSqlQueries.deleteAll() shouldBe "DELETE FROM $tableName;"
        }
    }
}