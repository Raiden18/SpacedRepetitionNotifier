package org.danceofvalkyries.utils.db

import org.danceofvalkyries.environment.Environment
import java.sql.Connection
import java.sql.DriverManager

class DataBaseImpl(
    private val environment: Environment
) : DataBase {

    private val connection = DriverManager.getConnection("jdbc:sqlite:${environment.pathToDb}")

    override fun establishConnection(): Connection {
        return connection
    }
}