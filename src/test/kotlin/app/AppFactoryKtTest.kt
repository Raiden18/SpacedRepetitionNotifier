package app

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.AppFactory
import org.danceofvalkyries.app.NotifierApp

class AppFactoryKtTest : FunSpec() {

    init {

        test("Should return NotifierApp") {
            val isNotifierApp = AppFactory()
                .create() is NotifierApp
            isNotifierApp shouldBe true
        }
    }
}