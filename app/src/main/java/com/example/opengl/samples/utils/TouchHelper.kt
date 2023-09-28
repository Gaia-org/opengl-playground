package com.example.opengl.samples.utils

import android.view.View
import java.lang.ref.WeakReference
import kotlin.math.sqrt

object TouchHelper {
    private lateinit var view: WeakReference<View>
    private var mSweepRatio: Float = 1.0f

    fun bindView(view: View) {
        this.view = WeakReference(view)
    }

    // 获取旋转轴
    fun getRotationAxis(dx: Float, dy: Float): FloatArray {
        val axis = floatArrayOf(0f, 0f, 1f)
        val dis = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        if (dis < 0.000001f) {
            return axis
        }
        // axis = (dx, dy, 0) X (0, 0, -1)
        axis[0] = -dy
        axis[1] = dx
        axis[2] = 0f
        return axis
    }

    // 获取旋转角度
    fun getRotationAngle(dx: Float, dy: Float): Float {
        val d = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        return d * mSweepRatio
    }

    // 获取滑动比例
    fun getSweepRatio(): Float {
        val realView: View = view.get() ?: return 1.0f
        val w = realView.width.toFloat()
        val h = realView.height.toFloat()
        mSweepRatio = 360 / sqrt((w * w + h * h).toDouble()).toFloat()
        return mSweepRatio
    }
}