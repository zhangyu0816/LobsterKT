package com.yimi.rentme.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.yimi.rentme.R

class LeanTextView : AppCompatTextView {
    private var mDegrees = 0

    constructor(context: Context?) : this(context!!, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        val a = context!!.obtainStyledAttributes(attrs, R.styleable.LeanTextView)
        mDegrees = a.getDimensionPixelSize(R.styleable.LeanTextView_degree, -45)
        a.recycle()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    )

    fun getDegrees(): Int {
        return mDegrees
    }

    fun setDegrees(mDegrees: Int) {
        this.mDegrees = mDegrees
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())
        canvas.rotate(mDegrees.toFloat(), this.width / 2f, this.height / 2f)
        super.onDraw(canvas)
        canvas.restore()
    }
}