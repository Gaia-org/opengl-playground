package com.example.opengl.samples.fbo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.opengl.GLES30
import android.util.Log
import com.example.opengl.samples.helper.EGLDisplayHelper
import com.example.opengl.samples.render.base.ObjType
import com.example.opengl.samples.render.base.RenderObjDispatcher
import com.example.opengl.samples.render.SimpleTextureRenderObj
import com.example.opengl.samples.utils.UiDisplayUtils
import java.nio.ByteBuffer

/**
 * Simple texture renderer for FBO drawing.
 */
class SimpleOffScreenRenderer {
    private lateinit var context: Context
    private var mViewRect: Rect = Rect()

    fun initialize(context: Context) {
        this.context = context
        EGLDisplayHelper.initializeEnv()
        val screenW = UiDisplayUtils.getScreenWidth(context)
        val screenH = UiDisplayUtils.getScreenHeight(context)
        mViewRect.set(0, 0, screenW, screenH)
        EGLDisplayHelper.initializeEGLSurface(screenW, screenH)
        EGLDisplayHelper.eglMakeCurrent()
        // 设置背景色
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        // 开启深度测试，避免出现遮挡
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        RenderObjDispatcher.initialize(ObjType.OBJ_SIMPLE_TEXTURE)
        val viewRect = (RenderObjDispatcher.getRenderObj(ObjType.OBJ_SIMPLE_TEXTURE) as SimpleTextureRenderObj).getRenderRect()
        // Note: 由于下面绘制过程中已经设置了矩阵变换来适配显示区域，所以此处暂无需设置窗口尺寸，否则画面会扭曲
        // GLES30.glViewport(viewRect.left, viewRect.top, viewRect.width(), viewRect.height())
        mViewRect.set(viewRect)
        // 创建帧缓存
        val frameBufferIds = IntArray(1)
        GLES30.glGenFramebuffers(1, frameBufferIds, 0) // 也可与纹理id共用
        // 绑定帧缓存
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferIds[0])
        val textureId = (RenderObjDispatcher.getRenderObj(ObjType.OBJ_SIMPLE_TEXTURE) as SimpleTextureRenderObj).getTextureId()
        Log.i(TAG, "initialize, textureId: $textureId")
        // 将纹理附着到帧缓存
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureId, 0)
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        Log.i(TAG, "onSurfaceChanged")
        EGLDisplayHelper.initializeEGLSurface(width, height)
        EGLDisplayHelper.eglMakeCurrent()
        if (mViewRect.isEmpty) {
            GLES30.glViewport(0, 0, width, height)
        }
        RenderObjDispatcher.onSurfaceChanged(width, height)
    }

    fun drawing(): Bitmap? {
        // 将颜色缓存区设置为预设的颜色
        //GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        RenderObjDispatcher.renderObj(ObjType.OBJ_SIMPLE_TEXTURE)
        Log.i(TAG, "drawing bitmap size: [${mViewRect.width()}, ${mViewRect.height()}]")
        val byteBuffer = ByteBuffer.allocate(mViewRect.width() * mViewRect.height() * Int.SIZE_BYTES)
        GLES30.glReadPixels(0, 0, mViewRect.width(), mViewRect.height(), GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, byteBuffer)
        val bitmap = Bitmap.createBitmap(mViewRect.width(), mViewRect.height(), Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(byteBuffer)
        return bitmap
    }

    companion object {
        private const val TAG = "SimpleOffScreenRenderer"
    }
}