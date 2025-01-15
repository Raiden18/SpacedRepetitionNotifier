package org.danceofvalkyries.utils.rest

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

fun ContentType(contentTypes: ContentTypes): Header {
    return Header(
        name = "ContentType",
        value = contentTypes.value,
    )
}

fun AuthorizationBearerHeader(token: String): Header {
    return Header(
        name = "Authorization",
        value = token,
    )
}

enum class ContentTypes(val value: String) {
    ApplicationJson("application/json")
}