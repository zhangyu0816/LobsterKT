package com.yimi.rentme.views.emojj

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.yimi.rentme.R

class EmojiEditText : AppCompatEditText {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init(attrs!!)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init(attrs!!)
    }

    constructor(context: Context?) : super(context!!) {
        mEmojiconSize = textSize.toInt()
    }

    private var mEmojiconSize = 0

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Emoji)
        mEmojiconSize = a.getDimension(R.styleable.Emoji_emojiSize, textSize).toInt()
        a.recycle()
        text = text
    }

    override fun onTextChanged(
        text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int
    ) {
        EmojiHandler.addEmojis(context, text as Spannable, mEmojiconSize)
    }

    /**
     * Set the size of emojicon in pixels.
     */
    fun setEmojiconSize(pixels: Int) {
        mEmojiconSize = pixels
    }
}