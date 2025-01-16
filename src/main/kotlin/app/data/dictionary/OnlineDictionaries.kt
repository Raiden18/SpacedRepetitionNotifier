package org.danceofvalkyries.app.data.dictionary

interface OnlineDictionaries {
    fun iterate(notionDbId: String): List<OnlineDictionary>
}