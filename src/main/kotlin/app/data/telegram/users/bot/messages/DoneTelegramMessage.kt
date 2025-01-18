package org.danceofvalkyries.app.data.telegram.users.bot.messages

import org.danceofvalkyries.utils.resources.StringResources

class DoneTelegramMessage(
    private val stringResources: StringResources
) : LocalTelegramMessage() {
    override val text: String
        get() = """${stringResources.getJob()} 😎 ${stringResources.everythingIsRevised()} ✅"""
}