package com.yimi.rentme.views.emoji

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.yimi.rentme.R

class EmojiTextView : AppCompatTextView {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init(attrs)
    }

    constructor(context: Context?) : super(context!!) {
        init(null)
    }

    private var mEmojiconSize = 0

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {
        if (attrs == null) {
            mEmojiconSize = textSize.toInt()
        } else {
            val a = context.obtainStyledAttributes(attrs, R.styleable.Emoji)
            mEmojiconSize = a.getDimension(R.styleable.Emoji_emojiSize, textSize).toInt()
            a.recycle()
        }
        text = text
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val builder = SpannableStringBuilder(text)
        EmojiHandler.addEmojis(context, builder, mEmojiconSize)
        super.setText(builder, type)
    }

    /**
     * Set the size of emojicon in pixels.
     */
    fun setEmojiconSize(pixels: Int) {
        mEmojiconSize = pixels
    }
}