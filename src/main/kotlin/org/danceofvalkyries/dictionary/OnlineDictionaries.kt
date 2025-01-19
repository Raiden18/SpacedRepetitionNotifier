package org.danceofvalkyries.dictionary

interface OnlineDictionaries {
    fun iterate(notionDbId: String): List<OnlineDictionary>
}