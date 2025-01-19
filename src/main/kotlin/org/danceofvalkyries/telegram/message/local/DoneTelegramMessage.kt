package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.utils.resources.StringResources

class DoneTelegramMessage(
    private val stringResources: StringResources
) : NotificationMessage() {

    override val text: String
        get() = """${stringResources.getJob()} 😎 ${stringResources.everythingIsRevised()} ✅"""
}