package org.danceofvalkyries.app.data.telegram.users

import kotlinx.coroutines.flow.Flow
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage

interface HumanUser : User {
    fun getActions(): Flow<TelegramMessage.Button.Callback>
}