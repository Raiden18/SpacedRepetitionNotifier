package utils

import org.danceofvalkyries.job.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.job.data.dictionary.OnlineDictionary

class OnlineDictionariesFake(
    private val dictionaries: List<OnlineDictionary>
) : OnlineDictionaries {
    override fun iterate(notionDbId: String): List<OnlineDictionary> {
        return dictionaries
    }
}