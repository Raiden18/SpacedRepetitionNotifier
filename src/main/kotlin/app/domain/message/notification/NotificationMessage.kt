package org.danceofvalkyries.app.domain.message.notification

import org.danceofvalkyries.app.domain.message.Message

abstract class NotificationMessage : Message {
    final override val type: String = "NOTIFICATION"
}