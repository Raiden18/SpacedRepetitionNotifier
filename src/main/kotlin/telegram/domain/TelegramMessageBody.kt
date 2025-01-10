package org.danceofvalkyries.telegram.domain

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup

data class TelegramMessageBody(
    val text: String,
    val nestedButtons: List<List<Button>>
)

fun TelegramMessageBody(
    text: String,
    buttons: List<Button>,
): TelegramMessageBody {
    return TelegramMessageBody(
        text = text,
        nestedButtons = buttons.map { listOf(it) }
    )
}

fun FlashCardMessage(flashCard: FlashCard): TelegramMessageBody {
    val body = StringBuilder()
        .appendLine("*${flashCard.memorizedInfo}*")
        .appendLine()
        .appendLine(flashCard.example)
        .appendLine()
        .appendLine("||${flashCard.answer}||")
        .appendLine()
        .appendLine("Choose:")
        .toString()

    return TelegramMessageBody(
        text = body,
        nestedButtons = listOf(
            listOf(Button("Forgot", ""), Button("Recalled", "")),
            listOf(
                Button(
                    text = "Look it up",
                    url = "https://dictionary.cambridge.org/dictionary/english/${flashCard.memorizedInfo}"
                ),
            )
        )
    )
}

fun RevisingIsNeededMessage(
    group: SpacedRepetitionDataBaseGroup
): TelegramMessageBody {
    return TelegramMessageBody(
        text = """You have ${group.totalFlashCardsNeedRevising} flashcards to revise 🧠""".trimIndent(),
        buttons = group.group
            .filter { it.flashCards.isNotEmpty() }
            .map {
                Button(
                    text = "${it.name}: ${it.flashCards.count()}",
                    url = "https://www.notion.so/databases/${it.id}"
                )
            }
    )
}

fun DoneMessage(): TelegramMessageBody {
    return TelegramMessageBody(
        text = """Good Job! 😎 Everything is revised! ✅""",
        buttons = emptyList()
    )
}