package com.example.opengl.samples.view

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import com.example.opengl.samples.helper.Grid
import com.example.opengl.samples.render.base.ObjType
import com.example.opengl.samples.render.base.RenderObjDispatcher
import com.example.opengl.samples.render.TextureBoxRenderObj
import com.example.opengl.samples.utils.TouchHelper
import javax.microedition.khronos.opengles.GL10

class SimpleGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyRenderer
    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        // 设置OpenGL ES版本为3.0
        setEGLContextClientVersion(3)
        renderer = MyRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        TouchHelper.bindView(this)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = x
                previousY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx: Float = x - previousX
                val dy: Float = previousY - y
                updateRotateAngle(dx, dy)
            }
        }
        return true
    }

    private fun updateRotateAngle(dx: Float, dy: Float) {
        TouchHelper.getSweepRatio()
        val axis = TouchHelper.getRotationAxis(dx, dy)
        val angle = TouchHelper.getRotationAngle(dx, dy)
        renderer.angle = angle
        renderer.axis = axis
        renderer.updateTxtBoxRotateArgs()
        requestRender()
    }

    private fun resetRotateAngle() {
        renderer.angle = 0f
        renderer.axis = floatArrayOf(0f, 0f, 1f)
        renderer.updateTxtBoxRotateArgs()
        requestRender()
    }


    private inner class MyRenderer : Renderer {
        @Volatile
        var angle: Float = 0f
        @Volatile
        var axis: FloatArray = floatArrayOf(0f, 0f, 1f)

        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        // vPMatrix is an abbreviation for "Model View Projection Matrix"
        private val vPMatrix = FloatArray(16)
        // Matrix for rotating
        private val rotationMatrix = FloatArray(16)
        private val grid: Grid = Grid()

        override fun onSurfaceCreated(unused: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
            GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
            // 开启深度测试，避免出现遮挡
//            unused?.glEnable(GLES30.GL_DEPTH_TEST) // Note: 二维模型渲染下关闭
            //grid.init()
            RenderObjDispatcher.initialize(ObjType.OBJ_SIMPLE_TEXTURE)
            val extensions = GLES30.glGetString(GLES30.GL_EXTENSIONS)
            Log.i(TAG, "get available opengl extensions: $extensions")
            GLES30.glGetString(GLES30.GL_VERSION).also {
                Log.i(TAG, "Current OpenGL version: $it")
            }
        }

        private fun initGlCoordinate() {
            // Set the camera position (View matrix)
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

            // Combine the projection and camera view matrices
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        }

        override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
            GLES30.glViewport(0, 0, width, height)
            // 计算投影和视图变换
            val ratio = width.toFloat() / height
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f)
            RenderObjDispatcher.onSurfaceChanged(width, height)
        }

        override fun onDrawFrame(unused: GL10) {
            val combinedMatrix = vPMatrix
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            initGlCoordinate()
            appendRotateMatrix(combinedMatrix)
            // 绘制网格
            //grid.draw(mvpMatrix);
            // Render content obj
            RenderObjDispatcher.renderObj(ObjType.OBJ_SIMPLE_TEXTURE, combinedMatrix)
        }

        /**
         * 叠加上旋转的矩阵转换效果
         */
        private fun appendRotateMatrix(scratch: FloatArray) {
            // Create a rotation transformation for the triangle
            val time = SystemClock.uptimeMillis() % 4000L
            //val angle = 0.090f * time.toInt()
            //Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)
            Matrix.setRotateM(rotationMatrix, 0, angle, axis[0], axis[1], axis[2])
            // Combine the rotation matrix with the projection and camera view
            // Note that the vPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        }

        fun updateTxtBoxRotateArgs() {
            val render = RenderObjDispatcher.getRenderObj(ObjType.OBJ_TEXTURE_BOX) as TextureBoxRenderObj
            render.mRotateAngle = angle
            render.mRotateAxis = axis
        }
    }



    companion object {
        private const val TAG = "SimpleGLSurfaceView"
        private const val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f
    }
}
