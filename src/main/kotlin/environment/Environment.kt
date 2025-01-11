package org.danceofvalkyries.environment

interface Environment {
    val homeDirectory: String
    val pathToDb: String
}

class EnvironmentImpl : Environment {

    override val homeDirectory: String
        get() = System.getProperty("user.home")

    override val pathToDb: String
        get() = "$homeDirectory/spaced_repetition.db"
}