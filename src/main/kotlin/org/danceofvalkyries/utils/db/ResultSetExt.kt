package org.danceofvalkyries.utils.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.sql.ResultSet

fun ResultSet.asFlow(): Flow<ResultSet> {
    return flow {
        while (next()) {
            emit(this@asFlow)
        }
        close()
    }
}

