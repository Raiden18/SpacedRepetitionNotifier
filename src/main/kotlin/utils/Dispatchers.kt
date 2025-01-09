package org.danceofvalkyries.utils

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatchers {
    val io: CoroutineDispatcher
}

class DispatchersImpl(override val io: CoroutineDispatcher) : Dispatchers