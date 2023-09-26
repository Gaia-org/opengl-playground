package com.example.opengl.samples.render

import android.opengl.GLES30
import android.util.Log
import com.example.opengl.samples.utils.RenderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Render a triangle that can adapt to the screen ratio to ensure that
 * it will not be stretched on different screens and achieve the same effect
 * as the coordinate system in OpenGL.
 * 渲染一个可自适应屏幕比例的三角形，保证不会在不同的屏幕上被拉伸，与opengl中的坐标系达到一致效果。
 */
class TriangleRenderObj : IRenderObj {

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of objects that use this vertex shader.
        "uniform mat4 uMVPMatrix;   \n" +
                "attribute vec4 vPosition;  \n" +
                "attribute vec4 vColor; \n" +
                "varying vec4 vertexColor; \n" +
                "void main(){               \n" +
                    // The matrix must be included as part of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "gl_Position = uMVPMatrix * vPosition; \n" +
                    "vertexColor = vColor; \n" +
                "}  \n"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec4 vertexColor;" +
                "void main() {" +
                "  gl_FragColor = vertexColor;" +
                "}"

    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private var positionHandle: Int = -1
    private var muMVPMatrixHandle: Int = -1
    private var colorHandle: Int = -1

    private var program: Int = -1

    override val objType: Int
        get() = ObjType.OBJ_TRIANGLE

    override fun initialize() {
        val vertexShader: Int = RenderUtil.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = RenderUtil.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = RenderUtil.createProgram(vertexShader, fragmentShader)

        positionHandle = GLES30.glGetAttribLocation(program, "vPosition")
        muMVPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix")
        colorHandle = GLES30.glGetAttribLocation(program, "vColor")
        Log.i(TAG, "color handle: $colorHandle")

        GLES30.glUseProgram(program)
    }

    override fun draw(mvpMatrix: FloatArray?) {
        // Apply the combined projection and camera view transformations
        // 将mvpMatrix数组中的矩阵数据传递给着色器程序中的muMVPMatrixHandle变量并应用
        mvpMatrix?.let {
            GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, it, 0)
        }
        // render
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        // set colors
        GLES30.glEnableVertexAttribArray(colorHandle)
        GLES30.glVertexAttribPointer(
            colorHandle,
            COUNT_PER_COLOR,
            GLES30.GL_FLOAT,
            false,
            0,
            colorBuffer
        )
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(colorHandle)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {

    }

    companion object {
        private const val TAG = "Triangle"
        private const val COORDS_PER_VERTEX = 3
        private const val COUNT_PER_COLOR = 4

        private val triangleCoords = floatArrayOf(
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
        )

        private val vertexColors = floatArrayOf(
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
        )

        private val vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(triangleCoords)
                position(0)
            }

        private val colorBuffer = ByteBuffer.allocateDirect(vertexColors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertexColors)
                position(0)
            }
    }
}