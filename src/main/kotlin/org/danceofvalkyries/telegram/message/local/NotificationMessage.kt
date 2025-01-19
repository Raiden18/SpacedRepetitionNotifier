package org.danceofvalkyries.telegram.message.local

abstract class NotificationMessage: LocalTelegramMessage() {

    final override val type: String
        get() = "NOTIFICATION"
}