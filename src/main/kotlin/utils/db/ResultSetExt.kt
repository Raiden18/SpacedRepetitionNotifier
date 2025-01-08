package org.danceofvalkyries.utils.db

import java.sql.ResultSet

fun ResultSet.asSequence(): Sequence<ResultSet> {
    return sequence {
        while (next()) {
            yield(this@asSequence)
        }
    }
}

