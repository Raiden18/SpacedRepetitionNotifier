package org.danceofvalkyries.utils

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

class DispatchersImpl : Dispatchers {
    override val io: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO
    override val default: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default
    override val unconfined: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Unconfined
}