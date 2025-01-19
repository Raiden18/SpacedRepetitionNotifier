package org.danceofvalkyries.dictionary.constant

import org.danceofvalkyries.config.domain.ObservedDatabase
import org.danceofvalkyries.dictionary.OnlineDictionaries
import org.danceofvalkyries.dictionary.OnlineDictionary

class ConfigOnlineDictionaries(
    private val observedDatabases: List<ObservedDatabase>
) : OnlineDictionaries {

    override fun iterate(notionDbId: String): List<OnlineDictionary> {
        return observedDatabases.asSequence()
            .map { it.id to it.dictionaries }
            .toMap()[notionDbId]
            ?.map { ConstantOnlineDictionary(it) }
            ?: emptyList()
    }
}