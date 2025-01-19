package unit.app

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import job.telegram_listener.TelegramButtonListenerJob
import org.danceofvalkyries.job.JobFactory
import org.danceofvalkyries.job.NotifierJob
import org.danceofvalkyries.job.SandBoxJob

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
    }
}