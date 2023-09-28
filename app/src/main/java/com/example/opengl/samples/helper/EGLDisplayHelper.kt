package com.example.opengl.samples.helper

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLUtils
import android.util.Log

/**
 * 用于创建和管理OpenGL ES的上下文以及与设备的显示表面(环境)。
 * @author Dorck
 * @since 2023/09/27
 */
object EGLDisplayHelper {
    private const val TAG = "EGLDisplayHelper"
    var eglDisplay: EGLDisplay? = EGL14.EGL_NO_DISPLAY
        private set
    var eglContext: EGLContext? = EGL14.EGL_NO_CONTEXT
        private set
    var eglSurface: EGLSurface? = EGL14.EGL_NO_SURFACE
        private set
    private var mEglConfig: EGLConfig? = null

    fun initializeEnv() {
        Log.i(TAG, "initializeEnv...")
        // Create and init EGLDisplay
        this.eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw IllegalStateException("Env built failed, eglGetDisplay method get null.")
        }
        val versions = IntArray(2)
        if (!EGL14.eglInitialize(eglDisplay, versions, 0, versions, 1)) {
            throw IllegalStateException("Env built failed, eglInitialize method get false.")
        }
        // Create configs and apply it.
        val eGLConfigs = arrayOfNulls<EGLConfig>(1)
        val eglConfigAttrs = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_DEPTH_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE,
            EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_NONE
        )
        if (!EGL14.eglChooseConfig(
                this.eglDisplay,
                eglConfigAttrs,
                0,
                eGLConfigs,
                0,
                1,
                IntArray(1),
                0
            )
        ) {
            throw IllegalStateException("Env built failed, eglChooseConfig method get false.")
        }
        this.mEglConfig = eGLConfigs.first()
        // Create EGLContext
        val eglContextAttrs = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        this.eglContext = EGL14.eglCreateContext(this.eglDisplay, eGLConfigs.first(), EGL14.EGL_NO_CONTEXT, eglContextAttrs, 0)
    }

    fun initializeEGLSurface(width: Int, height: Int) {
        // Create EGLSurface
        val eglSurfaceAttrs = intArrayOf(
            EGL14.EGL_WIDTH, width,
            EGL14.EGL_HEIGHT, height,
            EGL14.EGL_NONE
        )
        this.eglSurface = EGL14.eglCreatePbufferSurface(this.eglDisplay, mEglConfig, eglSurfaceAttrs, 0)
    }

    fun eglMakeCurrent() {
        if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw IllegalStateException("eglMakeCurrent failed, error: ${GLUtils.getEGLErrorString(EGL14.eglGetError())}")
        }
        Log.i(TAG, "eglMakeCurrent succeed.")
    }

    fun releaseEGLEnv() {
        Log.i(TAG, "releaseEGLEnv...")
        EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
        EGL14.eglDestroySurface(eglDisplay, eglSurface)
        EGL14.eglDestroyContext(eglDisplay, eglContext)
        EGL14.eglReleaseThread()
        EGL14.eglTerminate(eglDisplay)
        eglContext = EGL14.EGL_NO_CONTEXT
        eglSurface = EGL14.EGL_NO_SURFACE
        eglDisplay = EGL14.EGL_NO_DISPLAY
    }
}