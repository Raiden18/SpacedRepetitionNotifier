package integrations

import com.google.gson.Gson
import org.danceofvalkyries.app.data.telegram.jsons.MessageData
import org.danceofvalkyries.app.data.telegram.jsons.ReplyMarkupData
import org.danceofvalkyries.utils.rest.jsonObject

object TestData {

    val CHAT_ID = "123456789"
    val TELEGRAM_API_KEY = "BOT_API_KEY"

    private val OMITTED = "OMITTED"
    private val OMITTED_INT = -1

    object Notion {

        object GreekLetterAndSounds {

            fun unrevisedCardBodyRequest(): String {
                return jsonObject {
                    "filter" to jsonObject {
                        "and" to listOf(
                            jsonObject {
                                "property" to "Know Level 1"
                                "checkbox" to jsonObject {
                                    "equals" to true
                                }
                            },
                            jsonObject {
                                "property" to "Show"
                                "checkbox" to jsonObject {
                                    "equals" to true
                                }
                            }
                        )
                    }
                }.let { Gson().toJson(it) }
            }

            fun dataBaseResponse(
                id: String,
                name: String = "Greek Letters and Sounds",
            ): String {
                return jsonObject {
                    "object" to "database"
                    "id" to id
                    "cover" to null
                    "icon" to null
                    "created_time" to "2025-01-02T06:53:00.000Z"
                    "created_by" to jsonObject {
                        "object" to "user"
                        "id" to OMITTED
                    }
                    "last_edited_by" to jsonObject {
                        "object" to "user"
                        "id" to OMITTED
                    }
                    "last_edited_time" to "2025-01-13T15:32:00.000Z"
                    "title" to listOf(
                        jsonObject {
                            "type" to "text"
                            "text" to jsonObject {
                                "content" to name
                                "link" to null
                            }
                            "annotations" to jsonObject {
                                "bold" to false
                                "italic" to false
                                "strikethrough" to false
                                "underline" to false
                                "code" to false
                                "color" to "default"
                            }
                            "plain_text" to name
                            "href" to null
                        }
                    )
                    "description" to emptyList<Any>()
                    "is_inline" to true
                    "properties" to jsonObject {
                        "Know Level 7" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 7"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Know Level 6" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 6"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Interval 4" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 4"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "Interval 1" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 1"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "SRS" to jsonObject {
                            "id" to OMITTED
                            "name" to "SRS"
                            "type" to "formula"
                            "formula" to jsonObject {
                                "expression" to "OMITTED"
                            }
                        }
                        "Know Level 4" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 4"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Interval 6" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 6"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "Tags" to jsonObject {
                            "id" to OMITTED
                            "name" to "Tags"
                            "type" to "multi_select"
                            "multi_select" to jsonObject {
                                "options" to listOf<Any>()
                            }
                        }
                        "Updated Time" to jsonObject {
                            "id" to OMITTED
                            "name" to "Updated Time"
                            "type" to "last_edited_time"
                            "last_edited_time" to jsonObject { }
                        }
                        "Explanation" to jsonObject {
                            "id" to OMITTED
                            "name" to "Explanation"
                            "type" to "rich_text"
                            "rich_text" to jsonObject { }
                        }
                        "Show" to jsonObject {
                            "id" to OMITTED
                            "name" to "Show"
                            "type" to "formula"
                            "formula" to jsonObject {
                                "expression" to "OMITTED"
                            }
                        }
                        "Current Interval" to jsonObject {
                            "id" to OMITTED
                            "name" to "Current Interval"
                            "type" to "formula"
                            "formula" to jsonObject {
                                "expression" to "OMITTED"
                            }
                        }
                        "Know Level 2" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 2"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Interval 5" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 5"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "Review Date" to jsonObject {
                            "id" to OMITTED
                            "name" to "Review Date"
                            "type" to "formula"
                            "formula" to jsonObject {
                                "expression" to "OMITTED"
                            }
                        }
                        "Interval 2" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 2"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "Interval 7" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 7"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "Know Level 8" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 8"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Know Level 3" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 3"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Know Level 5" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 5"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Know Level 1" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 1"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Interval 3" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 3"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "Interval 9" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 9"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "Interval 8" to jsonObject {
                            "id" to OMITTED
                            "name" to "Interval 8"
                            "type" to "number"
                            "number" to jsonObject {
                                "format" to "number"
                            }
                        }
                        "Know Level 9" to jsonObject {
                            "id" to OMITTED
                            "name" to "Know Level 9"
                            "type" to "checkbox"
                            "checkbox" to jsonObject { }
                        }
                        "Name" to jsonObject {
                            "id" to "title"
                            "name" to "Name"
                            "type" to "title"
                            "title" to jsonObject { }
                        }
                    }
                    "parent" to jsonObject {
                        "type" to "page_id"
                        "page_id" to OMITTED
                    }
                    "url" to "OMITTED"
                    "public_url" to null
                    "archived" to false
                    "in_trash" to false
                    "request_id" to OMITTED
                }.let { Gson().toJson(it) }
            }

            fun pagesResponse(
                pages: List<Map<String, Any>>
            ): String {
                return jsonObject {
                    "object" to "list"
                    "results" to pages
                    "next_cursor" to null
                    "has_more" to false
                    "type" to "page_or_database"
                    "page_or_database" to jsonObject { }
                    "request_id" to OMITTED
                }.let { Gson().toJson(it) }
            }

            fun pageResponse(
                id: String,
                dataBaseId: String,
                name: String,
                explanation: String,
            ): Map<String, Any> {
                return jsonObject {
                    "object" to "page"
                    "id" to id
                    "created_time" to "2025-01-02T06:53:00.000Z"
                    "last_edited_time" to "2025-01-17T10:43:00.000Z"
                    "created_by" to jsonObject {
                        "object" to "user"
                        "id" to OMITTED
                    }
                    "last_edited_by" to jsonObject {
                        "object" to "user"
                        "id" to OMITTED
                    }
                    "cover" to null
                    "icon" to null
                    "parent" to jsonObject {
                        "type" to "database_id"
                        "database_id" to dataBaseId
                    }
                    "archived" to false
                    "in_trash" to false
                    "properties" to jsonObject {
                        "Know Level 7" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to false
                        }
                        "Know Level 6" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to false
                        }
                        "Interval 4" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 5
                        }
                        "Interval 1" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 0
                        }
                        "SRS" to jsonObject {
                            "id" to OMITTED
                            "type" to "formula"
                            "formula" to jsonObject {
                                "type" to "string"
                                "string" to "1 Level"
                            }
                        }
                        "Know Level 4" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to false
                        }
                        "Interval 6" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 13
                        }
                        "Tags" to jsonObject {
                            "id" to OMITTED
                            "type" to "multi_select"
                            "multi_select" to emptyList<String>()
                        }
                        "Updated Time" to jsonObject {
                            "id" to OMITTED
                            "type" to "last_edited_time"
                            "last_edited_time" to "2025-01-17T10:43:00.000Z"
                        }
                        "Explanation" to jsonObject {
                            "id" to OMITTED
                            "type" to "rich_text"
                            "rich_text" to listOf(
                                jsonObject {
                                    "type" to "text"
                                    "text" to jsonObject {
                                        "content" to explanation
                                        "link" to null
                                    }
                                    "annotations" to jsonObject {
                                        "bold" to true
                                        "italic" to false
                                        "strikethrough" to false
                                        "underline" to false
                                        "code" to false
                                        "color" to "default"
                                    }
                                    "plain_text" to explanation
                                    "href" to null
                                }
                            )
                        }
                        "Show" to jsonObject {
                            "id" to OMITTED
                            "type" to "formula"
                            "formula" to jsonObject {
                                "type" to "boolean"
                                "boolean" to true
                            }
                        }
                        "Current Interval" to jsonObject {
                            "id" to OMITTED
                            "type" to "formula"
                            "formula" to jsonObject {
                                "type" to "number"
                                "number" to 0
                            }
                        }
                        "Know Level 2" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to false
                        }
                        "Interval 5" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 8
                        }
                        "Review Date" to jsonObject {
                            "id" to OMITTED
                            "type" to "formula"
                            "formula" to jsonObject {
                                "type" to "date"
                                "date" to jsonObject {
                                    "start" to "2025-01-17T10:43:00.000+00:00"
                                    "end" to null
                                    "time_zone" to null
                                }
                            }
                        }
                        "Interval 2" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 0
                        }
                        "Interval 7" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 21
                        }
                        "Know Level 8" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to false
                        }
                        "Know Level 3" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to false
                        }
                        "Know Level 5" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to false
                        }
                        "Know Level 1" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to true
                        }
                        "Interval 3" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 0
                        }
                        "Interval 9" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 55
                        }
                        "Interval 8" to jsonObject {
                            "id" to OMITTED
                            "type" to "number"
                            "number" to 34
                        }
                        "Know Level 9" to jsonObject {
                            "id" to OMITTED
                            "type" to "checkbox"
                            "checkbox" to false
                        }
                        "Name" to jsonObject {
                            "id" to "title"
                            "type" to "title"
                            "title" to listOf(
                                jsonObject {
                                    "type" to "text"
                                    "text" to jsonObject {
                                        "content" to name
                                        "link" to null
                                    }
                                    "annotations" to jsonObject {
                                        "bold" to false
                                        "italic" to false
                                        "strikethrough" to false
                                        "underline" to false
                                        "code" to false
                                        "color" to "default"
                                    }
                                    "plain_text" to name
                                    "href" to null
                                }
                            )
                        }
                    }
                    "url" to OMITTED
                    "public_url" to null
                }
            }
        }
    }

    object Telegram {

        object SendMessage {

            fun notificationRequestWithOneButton(
                text: String,
                buttonTitle: String,
                notionDataBaseId: String,
            ): String {
                return jsonObject {
                    "chat_id" to CHAT_ID
                    "parse_mode" to "MarkdownV2"
                    "reply_markup" to jsonObject {
                        "inline_keyboard" to listOf(
                            listOf(
                                jsonObject {
                                    "text" to buttonTitle
                                    "callback_data" to "dbId=$notionDataBaseId"
                                }
                            )
                        )
                    }
                    "text" to text
                }.let { Gson().toJson(it) }
            }

            fun notificationResponseWithOneButton(messageId: Int, text: String = "Undefined"): String {
                return jsonObject {
                    "ok" to true
                    "result" to jsonObject {
                        "message_id" to messageId
                        "from" to jsonObject {
                            "id" to OMITTED_INT
                            "is_bot" to true
                            "first_name" to OMITTED
                            "username" to OMITTED
                        }
                        "chat" to jsonObject {
                            "id" to OMITTED_INT
                            "first_name" to OMITTED
                            "username" to OMITTED
                            "type" to "private"
                        }
                        "date" to 1737118400
                        "text" to text
                    }
                }.let { Gson().toJson(it) }
            }

            fun notificationDoneRequest(messageId: Long, text: String): String {
                return MessageData(
                    chatId = CHAT_ID,
                    text = text,
                    replyMarkup = ReplyMarkupData(
                        emptyList()
                    ),
                    parseMode = "MarkdownV2",
                    messageId = messageId,
                ).let { Gson().toJson(it) }
            }

            fun notificationDoneResponse(messageId: Long, text: String): String {
                return jsonObject {
                    "ok" to true
                    "result" to jsonObject {
                        "message_id" to messageId
                        "from" to jsonObject {
                            "id" to OMITTED_INT
                            "is_bot" to true
                            "first_name" to OMITTED
                            "username" to OMITTED
                        }
                        "chat" to jsonObject {
                            "id" to OMITTED_INT
                            "first_name" to OMITTED
                            "username" to OMITTED
                            "type" to "private"
                        }
                        "date" to 1737118400
                        "text" to text
                    }
                }.let { Gson().toJson(it) }
            }
        }

        object Urls {

            fun getDeleteMessageUrl(messageId: Long): String {
                return "https://api.telegram.org/bot$TELEGRAM_API_KEY/deleteMessage?chat_id=$CHAT_ID&message_id=$messageId"
            }
        }
    }
}