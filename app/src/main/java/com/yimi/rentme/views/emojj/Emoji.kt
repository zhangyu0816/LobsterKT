package com.yimi.rentme.views.emojj

import java.io.Serializable

class Emoji(emoji: String?) : Serializable {
    private var icon = 0
    private var value = 0.toChar()
    private var emoji: String? = null

    fun fromResource(icon: Int, value: Int): Emoji {
        val emoji = Emoji(emoji)
        emoji.icon = icon
        emoji.value = value.toChar()
        return emoji
    }

    fun fromCodePoint(codePoint: Int): Emoji {
        val emoji = Emoji(emoji)
        emoji.emoji = newString(codePoint)
        return emoji
    }

    fun fromChar(ch: Char): Emoji {
        val emoji = Emoji(emoji)
        emoji.emoji = ch.toString()
        return emoji
    }

    fun fromChars(chars: String?): Emoji {
        val emoji = Emoji(emoji)
        emoji.emoji = chars
        return emoji
    }

    fun getValue(): Char {
        return value
    }

    fun getIcon(): Int {
        return icon
    }

    fun getEmoji(): String? {
        return emoji
    }

    fun newString(codePoint: Int): String {
        return if (Character.charCount(codePoint) == 1) {
            codePoint.toString()
        } else {
            String(Character.toChars(codePoint))
        }
    }
}