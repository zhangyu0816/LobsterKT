package com.yimi.rentme.views.emojj

import android.content.Context
import android.text.Spannable
import com.yimi.rentme.R
import com.zb.baselibs.app.BaseApp
import java.lang.reflect.Field
import java.util.regex.Pattern

object EmojiHandler {
    var maxEmojiCount = 88
    val sCustomizeEmojisMap: MutableMap<Int, Int> = HashMap()
    private fun getPic(pid: String): Int {
        val f: Field
        try {
            f = R.mipmap::class.java.getField(pid)
            return f.getInt(null)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getCusEmojisResource(index: Int): Int {
        var key = 0
        val set: Set<*> = (sCustomizeEmojisMap as Map<*, *>).entries
        for (o in set) {
            val (key1, value) = o as Map.Entry<*, *>
            if (value == index) {
                key = key1 as Int
            }
        }
        return key
    }

    private fun cusEmojisResource(key: Int): Int {
        return sCustomizeEmojisMap[key]!!
    }

    /**
     * Convert emoji characters of the given Spannable to the according
     * emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     */
    fun addEmojis(context: Context?, text: Spannable, emojiSize: Int) {
        val length = text.length
        val oldSpans = text.getSpans(0, length, EmojiSpan::class.java)
        for (oldSpan in oldSpans) {
            text.removeSpan(oldSpan)
        }
        val m = "\\{f:\\d+\\}"
        val match = Pattern.compile(m).matcher(text.toString())
        while (match.find()) {
            val mSr = match.group()
            val start = mSr.indexOf("{f:")
            val end = mSr.indexOf("}")
            var content: String
            if (start > -1) {
                content = mSr.substring(3, end)
                val icon = cusEmojisResource(content.toInt())
                if (icon > 0) {
                    text.setSpan(
                        EmojiSpan(
                            context!!, icon, (BaseApp.W * (60f / 1080f)).toInt()
                        ), match.start(), match.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }

    init {
        // Customize
        for (i in 1 until maxEmojiCount) {
            sCustomizeEmojisMap[i] = getPic("emoji_$i")
        }
    }
}