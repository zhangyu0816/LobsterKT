package com.yimi.rentme.views.emoji

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText

object EmojiUtil {

    fun setProhibitEmoji(et: EditText) {
        val filters = arrayOf(getInputFilterProhibitEmoji())
        et.filters = filters
    }

    private fun getInputFilterProhibitEmoji(): InputFilter {
        return InputFilter { source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int ->
            val buffer = StringBuffer()
            var i = start
            while (i < end) {
                val codePoint = source[i]
                if (!getIsEmoji(codePoint)) {
                    buffer.append(codePoint)
                } else {
                    i++
                }
                i++
            }
            if (source is Spanned) {
                val sp = SpannableString(buffer)
                TextUtils.copySpansFrom(
                    source,
                    start,
                    end,
                    null,
                    sp,
                    0
                )
                return@InputFilter sp
            } else {
                return@InputFilter buffer
            }
        }
    }

    private fun getIsEmoji(codePoint: Char): Boolean {
        return codePoint.code != 0x0 && codePoint.code != 0x9 && codePoint.code != 0xA && codePoint.code != 0xD && (codePoint.code < 0x20 || codePoint.code > 0xD7FF) && (codePoint.code < 0xE000 || codePoint.code > 0xFFFD)
    }
}