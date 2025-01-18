package org.danceofvalkyries.app.data.telegram.users.bot.translator

//TODO: Remove and move implto FlashCard Message
interface TextTranslator {
    fun encode(from: String?): String?
}