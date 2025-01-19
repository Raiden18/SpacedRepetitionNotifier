package org.danceofvalkyries.utils.db

import java.sql.Connection
import java.sql.DriverManager

class DataBaseImpl(
    private val path: String
) : DataBase {

    private val connection by lazy { DriverManager.getConnection("jdbc:sqlite:${path}") }

    override fun establishConnection(): Connection {
        return connection
    }

    override fun demolishConnect() {
        connection.close()
    }
}