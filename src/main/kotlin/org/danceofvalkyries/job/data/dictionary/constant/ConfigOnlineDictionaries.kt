package org.danceofvalkyries.job.data.dictionary.constant

import org.danceofvalkyries.config.domain.ObservedDatabase
import org.danceofvalkyries.job.data.dictionary.OnlineDictionaries
import org.danceofvalkyries.job.data.dictionary.OnlineDictionary

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