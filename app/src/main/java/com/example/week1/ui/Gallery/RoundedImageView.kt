package com.example.week1

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val paint = Paint().apply { isAntiAlias = true }
    private var rectF: RectF? = null
    private var shader: BitmapShader? = null
    private val cornerRadius = 25.0f

    override fun onDraw(canvas: Canvas) {
        val bitmap = getBitmapFromDrawable()
        if (bitmap != null) {
            shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = shader

            if (rectF == null) {
                rectF = RectF(0.0f, 0.0f, width.toFloat(), height.toFloat())
            }

            canvas.drawRoundRect(rectF!!, cornerRadius, cornerRadius, paint)
        } else {
            super.onDraw(canvas)
        }
    }

    private fun getBitmapFromDrawable(): Bitmap? {
        drawable ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
