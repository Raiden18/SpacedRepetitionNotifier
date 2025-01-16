package app.domain.message

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.notion.api.models.NotionId

class ButtonActionTest : FunSpec() {

    init {
        test("Should return Forgotten Action") {
            ButtonAction.parse(
                "forgottenFlashCardId=228"
            ) shouldBe ButtonAction.Forgotten("228")
        }

        test("Should return Recalled Action") {
            ButtonAction.parse(
                "recalledFlashCardId=228"
            ) shouldBe ButtonAction.Recalled("228")
        }

        test("Should return DataBase Action") {
            ButtonAction.parse(
                "dbId=228"
            ) shouldBe ButtonAction.DataBase("228")
        }
    }
}