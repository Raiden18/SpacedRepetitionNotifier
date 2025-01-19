package integrations.testdata.telegram

import org.danceofvalkyries.job.data.telegram.message.TelegramMessage

data class TelegramCallbackDataFake(
    override val id: String,
    override val messageId: Long,
    override val action: TelegramMessage.Button.Action,
) : TelegramMessage.Button.Callback {

    private var answeredCallbackIds = listOf<String>()

    override suspend fun answer() {
        answeredCallbackIds = answeredCallbackIds + listOf(id)
    }

    fun assertThat(): Matcher {
        return Matcher()
    }

    inner class Matcher {

        fun callbackIsAnswered(id: String) {
            if (answeredCallbackIds.contains(id).not()) {
                val stringBuilder = StringBuilder()
                    .appendLine("Callback is not answered.")
                    .appendLine("CallbackId: $id")
                    .appendLine("List: $answeredCallbackIds")
                    .toString()
                error(stringBuilder)
            }
        }
    }
}