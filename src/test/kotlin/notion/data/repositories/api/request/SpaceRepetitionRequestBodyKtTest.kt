package notion.data.repositories.api.request

import com.google.gson.GsonBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.data.repositories.api.models.request.SpacedRepetitionRequestBody

class SpaceRepetitionRequestBodyKtTest : FunSpec() {

    init {
        test("Should create body for Notion Database") {
            val gson = GsonBuilder()
                .setPrettyPrinting()
                .create()
            SpacedRepetitionRequestBody(
                gson
            ) shouldBe """
                {
                  "filter": {
                    "and": [
                      {
                        "property": "Know Level 1",
                        "checkbox": {
                          "equals": true
                        }
                      },
                      {
                        "property": "Show",
                        "checkbox": {
                          "equals": true
                        }
                      }
                    ]
                  }
                }
            """.trimIndent()
        }
    }
}