package org.danceofvalkyries.config.domain

interface Config {
    val notion: NotionConfig
    val telegram: TelegramConfig
    val flashCardsThreshold: Int
}

interface NotionConfig {
    val apiKey: String
    val observedDatabases: List<String>
    val delayBetweenRequests: Int
}

interface TelegramConfig {
    val apiKey: String
    val chatId: String
}
