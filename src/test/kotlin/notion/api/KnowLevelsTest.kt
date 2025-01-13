package notion.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.api.models.KnowLevels

class KnowLevelsTest : FunSpec() {

    init {
        test("Should disable all") {
            KnowLevels(
                mapOf(
                    1 to true,
                    2 to true,
                )
            ).disableAll() shouldBe KnowLevels(
                mapOf(
                    1 to false,
                    2 to false,
                )
            )
        }

        test("Should move to next level") {
            val levels = KnowLevels(
                mapOf(
                    1 to false,
                    2 to false,
                )
            )

            val next1 = levels.next()
                .next()
                .next()
            next1 shouldBe KnowLevels(
                mapOf(
                    1 to true,
                    2 to true
                )
            )

        }
    }
}