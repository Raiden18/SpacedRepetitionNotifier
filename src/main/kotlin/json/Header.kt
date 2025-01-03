package org.danceofvalkyries.json

interface Header {
    val name: String
    val value: String
}

fun Header(
    name: String,
    value: String,
): Header {
    return object : Header {
        override val name: String = name
        override val value: String = value
    }
}

fun ContentTypeApplicationJson(): Header {
    return Header(
        name = "ContentType",
        value = "application/json",
    )
}

fun AuthorizationBearerHeader(token: String): Header {
    return Header(
        name = "Authorization",
        value = token,
    )
}