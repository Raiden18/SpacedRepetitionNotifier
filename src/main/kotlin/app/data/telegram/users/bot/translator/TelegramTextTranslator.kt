package org.danceofvalkyries.app.data.telegram.users.bot.translator

class TelegramTextTranslator : TextTranslator {

    override fun encode(from: String?): String? {
        from ?: return null
        return from
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("|", "\\|")
            .replace("#", "\\#")
            .replace("<", "\\<")
            .replace(">", "\\>")
            .replace("`", "\\`")
            .replace("~", "\\~")
            .replace("[", "\\[")
            .replace("]", "\\]")
            .replace("*", "\\*")
            .replace("!", "\\!")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("=", "\\=")
            .replace(".", "\\.")
            .replace("_", "\\_")
            .replace("-", "\\-")
            .replace("+", "\\+")
            .replace("\\\\", "\\")

    }
}