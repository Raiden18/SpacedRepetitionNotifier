package unit.app.data.dictionary.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.dictionary.constant.ConstantOnlineDictionary
import org.danceofvalkyries.app.data.dictionary.constant.ConfigOnlineDictionaries
import org.danceofvalkyries.config.domain.ObservedDatabase

class ConfigOnlineDictionariesTest : FunSpec() {

    private val dbWithDictionary = ObservedDatabase("1", listOf("https://dictionary.cambridge.org/dictionary/english/"))
    private val dbWithNoDictionary = ObservedDatabase("2", emptyList())

    init {
        test("Should return Online dictionaries for flash card if they exist") {
            ConfigOnlineDictionaries(
                listOf(dbWithDictionary, dbWithNoDictionary)
            ).iterate("1") shouldBe listOf(ConstantOnlineDictionary("https://dictionary.cambridge.org/dictionary/english/"))
        }

        test("Should return empty list if there are no dictionaries for flash card") {
            ConfigOnlineDictionaries(
                listOf(dbWithDictionary, dbWithNoDictionary)
            ).iterate("2") shouldBe emptyList()
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