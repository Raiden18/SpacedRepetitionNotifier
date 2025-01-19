package org.danceofvalkyries.utils.db

import java.sql.Connection

interface DataBase {
    fun establishConnection(): Connection
}