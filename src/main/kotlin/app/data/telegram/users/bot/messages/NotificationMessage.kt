package org.danceofvalkyries.app.data.telegram.users.bot.messages

abstract class NotificationMessage: LocalTelegramMessage() {

    final override val type: String
        get() = "NOTIFICATION"
}