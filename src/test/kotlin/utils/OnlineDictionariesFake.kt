package utils

import org.danceofvalkyries.dictionary.OnlineDictionaries
import org.danceofvalkyries.dictionary.OnlineDictionary

class OnlineDictionariesFake(
    private val dictionaries: List<OnlineDictionary>
) : OnlineDictionaries {
    override fun iterate(notionDbId: String): List<OnlineDictionary> {
        return dictionaries
    }
}