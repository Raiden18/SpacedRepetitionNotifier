package app.data.persistance.notion.page.dao.flashcard

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.FlashCardDbEntity
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.dao.FlashCardSqlQueries
import org.danceofvalkyries.utils.db.tables.columns.PrimaryKey
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class FlashCardSqlQueriesTest : FunSpec() {
    private val tableName = "flash_cards"

    private val id = TextTableColumn(
        name = "id",
        primaryKey = PrimaryKey(),
    )

    private val example = TextTableColumn("example")
    private val answer = TextTableColumn("answer")
    private val imageUrl = TextTableColumn("image_url")
    private val memorizedInfo = TextTableColumn("memorized_info")
    private val notionDbId = TextTableColumn("notion_db_id")
    private val flashcard = FlashCardDbEntity(
        memorizedInfo = "1",
        example = "2",
        answer = "3",
        imageUrl = "4",
        cardId = "5",
        notionDbId = "6",
    )

    private lateinit var flashCardSqlQueries: FlashCardSqlQueries

    init {
        beforeTest {
            flashCardSqlQueries = FlashCardSqlQueries(
                tableName,
                id,
                example,
                answer,
                imageUrl,
                memorizedInfo,
                notionDbId,
            )
        }

        test("Should create if not exist table query") {
            flashCardSqlQueries
                .createTableIfNotExisted() shouldBe "CREATE TABLE IF NOT EXISTS $tableName (id TEXT PRIMARY KEY, example TEXT, answer TEXT, image_url TEXT, memorized_info TEXT, notion_db_id TEXT);"
        }

        test("Should create select query for flashcards from specific notion db") {
            val dbId = "228"
            flashCardSqlQueries
                .selectAll(dbId) shouldBe "SELECT * FROM $tableName WHERE notion_db_id = '$dbId';"
        }

        test("Should create insert statement") {
            flashCardSqlQueries
                .insert(flashcard) shouldBe "INSERT INTO $tableName (id, example, answer, image_url, memorized_info, notion_db_id) VALUES ('5', '2', '3', '4', '1', '6');"
        }

        test("Should create insert statements with NULL") {
            val flashcard = FlashCardDbEntity(
                memorizedInfo = "1",
                example = null,
                answer = null,
                imageUrl = null,
                cardId = "5",
                notionDbId = "6",
            )

            flashCardSqlQueries
                .insert(
                    flashcard
                ) shouldBe "INSERT INTO $tableName (id, example, answer, image_url, memorized_info, notion_db_id) VALUES ('5', NULL, NULL, NULL, '1', '6');"
        }

        test("Should create delete statement") {
            flashCardSqlQueries
                .delete(
                    flashcard.cardId
                ) shouldBe "DELETE FROM $tableName WHERE id = '5';"
        }

        test("Should create delete all statements") {
            flashCardSqlQueries.deleteAll() shouldBe "DELETE FROM $tableName;"
        }
    }
}