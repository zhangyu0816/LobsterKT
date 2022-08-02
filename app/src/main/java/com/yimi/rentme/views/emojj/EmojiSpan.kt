package com.yimi.rentme.views.emojj

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan

internal class EmojiSpan(
    private val mContext: Context,
    private val mResourceId: Int,
    private val mSize: Int
) :
    DynamicDrawableSpan() {
    private var mDrawable: Drawable? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getDrawable(): Drawable {
        if (mDrawable == null) {
            try {
                mDrawable = mContext.resources.getDrawable(mResourceId)
                val size = mSize
                mDrawable!!.setBounds(0, 0, size, size)
            } catch (ignored: Exception) {
            }
        }
        return mDrawable!!
    }
}