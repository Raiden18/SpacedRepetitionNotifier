package org.danceofvalkyries.app.data.telegram.users.bot.translator

interface TextTranslator {
    fun encode(from: String?): String?
}