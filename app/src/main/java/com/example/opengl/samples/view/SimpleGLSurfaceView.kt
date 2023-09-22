package com.example.opengl.samples.view

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.opengl.samples.render.TriangleRenderer
import javax.microedition.khronos.opengles.GL10

class SimpleGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyRenderer

    init {
        // 设置OpenGL ES版本为3.0
        setEGLContextClientVersion(2)
        renderer = MyRenderer()
        setRenderer(renderer)
    }

    private inner class MyRenderer : Renderer {

        private val triangle: TriangleRenderer = TriangleRenderer()

        override fun onSurfaceCreated(unused: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            triangle.init()
        }

        override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
            GLES30.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(unused: GL10) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            triangle.draw()
        }
    }
}
