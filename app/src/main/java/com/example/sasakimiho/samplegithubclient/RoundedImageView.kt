package com.example.sasakimiho.samplegithubclient

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class RoundedImageView : AppCompatImageView {
    val maskedPaint: Paint = Paint()
    val copyPaint: Paint = Paint()
    val maskDrawable: Drawable? = resources.getDrawable(R.drawable.base_drawable)

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {


    }

    var rect: Rect? = null
    var rectF: RectF? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rect = Rect(0, 0, w, h)
        rectF = RectF(rect)
    }

    override fun onDraw(canvas: Canvas?) {

        val cs: Int = canvas?.saveLayer(rectF,
                copyPaint,
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG or Canvas.FULL_COLOR_LAYER_SAVE_FLAG) as Int
        maskDrawable?.setBounds(rect)
        maskDrawable?.draw(canvas)
        canvas.saveLayer(rectF, maskedPaint, 0)
        canvas.restoreToCount(cs)
    }
}