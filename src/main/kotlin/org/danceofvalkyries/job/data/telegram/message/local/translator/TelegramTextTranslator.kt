package org.danceofvalkyries.job.data.telegram.message.local.translator

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