package utils

import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.dictionary.OnlineDictionary

class OnlineDictionariesFake(
    private val dictionaries: List<OnlineDictionary>
) : OnlineDictionaries {
    override fun iterate(notionDbId: String): List<OnlineDictionary> {
        return dictionaries
    }
}