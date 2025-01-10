package notion.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.domain.models.ImageUrl

class ImageUrlTest : FunSpec() {

    init {
        test("Telegram does not support images from shuttershock") {
            ImageUrl(
                "https://www.shutterstock.com/image-vector/cute-baby-regurgitating-after-eating-260nw-2016458315.jpg"
            ).isSupportedByTelegram shouldBe false
        }

        test("Telegram supports images") {
            ImageUrl(
                "https://valid-domain.com/cute-baby-regurgitating-after-eating-260nw-2016458315.jpg"
            ).isSupportedByTelegram shouldBe true
        }
    }
}