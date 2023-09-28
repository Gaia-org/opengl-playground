package com.example.opengl.samples.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.example.opengl.samples.fbo.SimpleOffScreenRenderer

/**
 * Simple custom view for rendering pixels from opengl.
 */
class SimpleRenderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var curBitmap: Bitmap? = null
    private val mSimpleOffScreenRenderer = SimpleOffScreenRenderer()

    init {
        mSimpleOffScreenRenderer.initialize(getContext())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.BLUE)
        val bitmap = mSimpleOffScreenRenderer.drawing()
        bitmap?.let {
            this.curBitmap = it
            canvas?.drawBitmap(bitmap, 0f, 0f, null)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mSimpleOffScreenRenderer.onSurfaceChanged(w, h)
    }
}