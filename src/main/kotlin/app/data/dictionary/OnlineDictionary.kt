package org.danceofvalkyries.app.data.dictionary

interface OnlineDictionary {
    fun getUrlFor(word: String): String
}