package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.apps.buttonslistener.domain.usecases.GetOnlineDictionariesForFlashCard
import org.danceofvalkyries.config.domain.ObservedDatabase
import org.danceofvalkyries.dictionary.api.OnlineDictionary
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId

class GetOnlineDictionariesForFlashCardKtTest : FunSpec() {

    private val dbWithDictionary = ObservedDatabase("1", listOf("https://dictionary.cambridge.org/dictionary/english/"))
    private val dbWithNoDictionary = ObservedDatabase("2", emptyList())

    init {

        test("Should return Online dictionaries for flash card if they exist") {
            val flashCardWithRequiredDictionary = FlashCardNotionPage.EMPTY.copy(
                notionDbID = NotionId("1")
            )
            GetOnlineDictionariesForFlashCard(
                listOf(dbWithDictionary, dbWithNoDictionary)
            ).execute(flashCardWithRequiredDictionary) shouldBe listOf(
                OnlineDictionary("https://dictionary.cambridge.org/dictionary/english/")
            )
        }

        test("Should return empty list if there are no dictionaries for flash card") {
            val flashCardWithRequiredDictionary = FlashCardNotionPage.EMPTY.copy(
                notionDbID = NotionId("2")
            )
            GetOnlineDictionariesForFlashCard(
                listOf(dbWithDictionary, dbWithNoDictionary)
            ).execute(flashCardWithRequiredDictionary) shouldBe emptyList()
        }
    }

    private fun ObservedDatabase(
        id: String,
        dictionaries: List<String>
    ): ObservedDatabase {
        return object : ObservedDatabase {
            override val id: String = id
            override val dictionaries: List<String>
                get() = dictionaries

        }
    }
}