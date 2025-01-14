package app

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.AppFactory
import org.danceofvalkyries.app.NotifierApp
import org.danceofvalkyries.app.TelegramButtonListenerApp
import org.danceofvalkyries.app.SandBoxApp

class AppFactoryKtTest : FunSpec() {

    init {
        test("Should return NotifierApp") {
            val isNotifierApp = AppFactory(arrayOf("notifier"))
                .create() is NotifierApp
            isNotifierApp shouldBe true
        }

        test("Should return TelegramButtonListenerApp") {
            val isTgButtonListenerApp = AppFactory(arrayOf("button_listener"))
                .create() is TelegramButtonListenerApp
            isTgButtonListenerApp shouldBe true
        }

        test("Should return SandBoxApp") {
            val isSandBoxApp = AppFactory(arrayOf("sand_box"))
                .create() is SandBoxApp
            isSandBoxApp shouldBe true
        }
    }
}