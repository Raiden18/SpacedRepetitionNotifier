package org.danceofvalkyries.telegram.domain

import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup

data class TelegramMessageBody(
    val text: String,
    val buttons: List<Button>
) {
    companion object {

        fun revisingIsNeeded(group: SpacedRepetitionDataBaseGroup): TelegramMessageBody {
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

        fun done(): TelegramMessageBody {
            return TelegramMessageBody(
                text = """Good Job! 😎 Everything is revised! ✅""",
                buttons = emptyList()
            )
        }
    }
}