package utils

import kotlinx.coroutines.CoroutineDispatcher
import org.danceofvalkyries.utils.Dispatchers

class DispatchersFake(
    private val dispatcher: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Unconfined
) : Dispatchers {
    override val io: CoroutineDispatcher
        get() = dispatcher
    override val default: CoroutineDispatcher
        get() = dispatcher
}