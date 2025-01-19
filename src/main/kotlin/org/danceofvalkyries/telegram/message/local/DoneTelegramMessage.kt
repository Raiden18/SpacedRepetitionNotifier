package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.utils.resources.StringResources

class DoneTelegramMessage(
    private val stringResources: StringResources
) : NotificationMessage() {

    override fun getText(): String {
        return """${stringResources.getJob()} 😎 ${stringResources.everythingIsRevised()} ✅"""
    }
}