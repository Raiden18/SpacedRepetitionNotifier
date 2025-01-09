package org.danceofvalkyries.utils

import kotlin.time.TimeSource

inline fun <T : Any> printMeasure(
    message: String,
    block: () -> T
): T {
    val markNow = TimeSource.Monotonic.markNow()
    val result = block.invoke()
    val spent = markNow.elapsedNow()
    println("$message: $spent")
    return result
}