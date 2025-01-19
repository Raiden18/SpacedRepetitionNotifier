package org.danceofvalkyries.job.data.dictionary

interface OnlineDictionary {
    fun getUrlFor(word: String): String
}