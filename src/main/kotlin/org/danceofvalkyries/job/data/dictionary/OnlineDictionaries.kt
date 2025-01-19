package org.danceofvalkyries.job.data.dictionary

interface OnlineDictionaries {
    fun iterate(notionDbId: String): List<OnlineDictionary>
}