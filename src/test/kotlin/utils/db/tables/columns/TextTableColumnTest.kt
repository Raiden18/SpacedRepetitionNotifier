package utils.db.tables.columns

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class TextTableColumnTest : FunSpec() {

    private val column: TextTableColumn = TextTableColumn("name")

    init {
        test("Sql value should be NULL if value is null") {
            column.sqlRequestValue(null) shouldBe "NULL"
        }

        test("Should escape ' symbol") {
            val text = "someone's daughter"
            column.sqlRequestValue(text) shouldBe "'someone''s daughter'"
        }
    }
}