package org.danceofvalkyries.utils.rest.clients.sever

import kotlinx.coroutines.flow.Flow

interface SeverClient {
    fun getWebHook(): Flow<String>
}