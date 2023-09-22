package com.example.opengl.samples.view

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.opengl.samples.helper.Grid
import com.example.opengl.samples.render.TriangleRenderer
import javax.microedition.khronos.opengles.GL10

class SimpleGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyRenderer

    init {
        // 设置OpenGL ES版本为3.0
        setEGLContextClientVersion(3)
        renderer = MyRenderer()
        setRenderer(renderer)
    }

    private inner class MyRenderer : Renderer {

        private val triangle: TriangleRenderer = TriangleRenderer()
        private val projectionMatrix = FloatArray(16)
        private val modelMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        private val mvpMatrix = FloatArray(16)
        private val grid: Grid = Grid()

        override fun onSurfaceCreated(unused: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            grid.init()
            triangle.init()
        }

        override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
            GLES30.glViewport(0, 0, width, height)
            // 计算投影和视图变换
            val ratio = width.toFloat() / height
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f);
            // 设置模型视图矩阵
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f,
                0f, 0f, 0f, 0f, 1f, 0f)
        }

        override fun onDrawFrame(unused: GL10) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            // 计算模型视图投影矩阵
            val modelViewProjectionMatrix = FloatArray(16)
            val identityMatrix = floatArrayOf(
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, identityMatrix, 0)

            // 绘制网格
            grid.draw(mvpMatrix);
            triangle.draw()
        }
    }
}
