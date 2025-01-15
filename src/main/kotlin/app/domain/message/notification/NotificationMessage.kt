package org.danceofvalkyries.app.domain.message.notification

import org.danceofvalkyries.app.domain.message.Message

abstract class NotificationMessage : Message {
    companion object {
        const val TYPE_NAME = "NOTIFICATION"
    }

    final override val type: String = TYPE_NAME
}