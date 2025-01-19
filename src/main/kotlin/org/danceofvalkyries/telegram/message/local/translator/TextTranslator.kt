package org.danceofvalkyries.telegram.message.local.translator

interface TextTranslator {
    fun encode(from: String?): String?
}