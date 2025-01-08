package utils.db

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.utils.db.DataBasePaths

class DatabasePaths : FunSpec() {

    private val dataBasePaths = DataBasePaths("/Users/paul")

    init {
        test("Should return path to db for dev") {
            dataBasePaths.development() shouldBe "/Users/paul/spaced_repetition_dev.db"
        }

        test("Should return path to db for prod") {
            dataBasePaths.production() shouldBe "/Users/paul/spaced_repetition.db"
        }
    }
}