package unit.app

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.job.JobFactory

class JobFactoryKtTest : FunSpec() {

    init {
        test("Should return Notifier Job") {
            JobFactory(arrayOf("notifier", "test"))
                .create().type shouldBe "notifier"
        }

        test("Should return ListenToTelegramEvents Job") {
            JobFactory(arrayOf("button_listener", "test"))
                .create().type shouldBe "button_listener"
        }

        test("Should return SandBox Job") {
            JobFactory(arrayOf("sand_box", "test"))
                .create().type shouldBe "sand_box"
        }

        test("Should return Update Cache Job") {
            JobFactory(arrayOf("update_cache", "test"))
                .create().type shouldBe "update_cache"
        }
    }
}