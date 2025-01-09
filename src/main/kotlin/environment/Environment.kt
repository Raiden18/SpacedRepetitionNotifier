package org.danceofvalkyries.environment

interface Environment {
    val homeDirectory: String
}

class EnvironmentImpl : Environment {
    override val homeDirectory: String
        get() = System.getProperty("user.home")
}