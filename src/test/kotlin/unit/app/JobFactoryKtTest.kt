package unit.app

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.job.telegram_listener.TelegramButtonListenerJob
import org.danceofvalkyries.job.JobFactory
import org.danceofvalkyries.job.NotifierJob
import org.danceofvalkyries.job.SandBoxJob
import org.danceofvalkyries.job.UpdateCacheJob

class JobFactoryKtTest : FunSpec() {

    init {
        test("Should return Notifier Job") {
            val isNotifier = JobFactory(arrayOf("notifier", "test"))
                .create() is NotifierJob
            isNotifier shouldBe true
        }

        test("Should return ListenToTelegramEvents Job") {
            val isTgButtonListener = JobFactory(arrayOf("button_listener", "test"))
                .create() is TelegramButtonListenerJob
            isTgButtonListener shouldBe true
        }

        test("Should return SandBox Job") {
            val isSandBox = JobFactory(arrayOf("sand_box", "test"))
                .create() is SandBoxJob
            isSandBox shouldBe true
        }

        test("Should return Update Cache Job") {
            val isUpdateCache = JobFactory(arrayOf("update_cache", "test"))
                .create() is UpdateCacheJob
            isUpdateCache shouldBe true
        }
    }
}