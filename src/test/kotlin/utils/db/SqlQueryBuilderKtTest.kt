package utils.db

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.utils.db.SqlQuery
import org.danceofvalkyries.utils.db.tables.columns.TextTableColumn

class SqlQueryBuilderKtTest : FunSpec() {

    private val idColumn = TextTableColumn("id")
    private val nameColumn = TextTableColumn("name")
    private val textColumn = TextTableColumn("text")

    init {
        test("Should create Update query with 1 parameter") {
            SqlQuery {
                update("table_name")
                set(nameColumn to "228")
                where(idColumn to "1")
            } shouldBe "UPDATE table_name SET name = '228' WHERE id = '1';"
        }

        test("Should create Update query with 2 parameters") {
            SqlQuery {
                update("table_name")
                set(listOf(nameColumn to "228", textColumn to "322"))
                where(idColumn to "1")
            } shouldBe "UPDATE table_name SET name = '228', text = '322' WHERE id = '1';"
        }
    }
}