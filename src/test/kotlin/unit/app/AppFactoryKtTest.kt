package unit.app

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.AppFactory
import org.danceofvalkyries.app.apps.notifier.NotifierApp
import org.danceofvalkyries.app.apps.SandBoxApp
import org.danceofvalkyries.app.apps.buttonslistener.TelegramButtonListenerApp

class AppFactoryKtTest : FunSpec() {

    init {
        test("Should return NotifierApp") {
            val isNotifierApp = AppFactory(arrayOf("notifier", "test"))
                .create() is NotifierApp
            isNotifierApp shouldBe true
        }

        test("Should return TelegramButtonListenerApp") {
            val isTgButtonListenerApp = AppFactory(arrayOf("button_listener", "test"))
                .create() is TelegramButtonListenerApp
            isTgButtonListenerApp shouldBe true
        }

        test("Should return SandBoxApp") {
            val isSandBoxApp = AppFactory(arrayOf("sand_box", "test"))
                .create() is SandBoxApp
            isSandBoxApp shouldBe true
        }
    }
}