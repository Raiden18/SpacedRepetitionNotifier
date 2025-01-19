package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.telegram.message.local.translator.TelegramTextTranslator
import org.danceofvalkyries.utils.resources.StringResources

class DoneTelegramMessage(
    private val stringResources: StringResources
) : NotificationMessage() {

    override fun getText(): String {
        val translator = TelegramTextTranslator()
        return translator.encode("""${stringResources.getJob()} 😎 ${stringResources.everythingIsRevised()} ✅""")!!
    }
}