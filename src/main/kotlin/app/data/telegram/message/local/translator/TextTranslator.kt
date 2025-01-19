package org.danceofvalkyries.app.data.telegram.message.local.translator

//TODO: Remove and move implto FlashCard Message
interface TextTranslator {
    fun encode(from: String?): String?
}