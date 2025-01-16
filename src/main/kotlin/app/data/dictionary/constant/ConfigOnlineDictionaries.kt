package org.danceofvalkyries.app.data.dictionary.constant

import org.danceofvalkyries.app.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.app.data.dictionary.OnlineDictionary
import org.danceofvalkyries.config.domain.ObservedDatabase

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