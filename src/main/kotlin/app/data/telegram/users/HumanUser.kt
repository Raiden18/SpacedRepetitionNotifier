package org.danceofvalkyries.app.data.telegram.users

interface HumanUser : User {
    suspend fun forget(flashCardId: String)
    suspend fun recall(flashCardId: String)
}