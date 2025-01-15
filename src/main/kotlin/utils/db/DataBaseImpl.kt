package org.danceofvalkyries.utils.db

import java.sql.Connection
import java.sql.DriverManager

class DataBaseImpl(
    private val path: String
) : DataBase {

    override fun establishConnection(): Connection {
        return DriverManager.getConnection("jdbc:sqlite:${path}")
    }
}