package org.danceofvalkyries.dictionary

interface OnlineDictionary {
    fun getUrlFor(word: String): String
}