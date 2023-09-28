package com.example.opengl.samples.render

import android.opengl.GLES30
import android.opengl.Matrix
import com.example.opengl.samples.render.base.BaseRenderObj
import com.example.opengl.samples.render.base.ObjType
import com.example.opengl.samples.utils.RenderUtil
// TODO: why some planes of cube not rendered
class CubeRenderObj : BaseRenderObj() {
    override val vertexShaderCode: String
        get() = "uniform mat4 uMVPMatrix;   \n" +
                "attribute vec4 vPosition;  \n" +
                "attribute vec4 vColor; \n" +
                "varying vec4 vertexColor; \n" +
                "void main(){               \n" +
                "gl_Position = uMVPMatrix * vPosition; \n" +
                "vertexColor = vColor; \n" +
                "}  \n"
    override val fragmentShaderCode: String
        get() = "precision mediump float; \n" +
                "varying vec4 vertexColor; \n" +
                "void main() { \n" +
                "  gl_FragColor = vertexColor; \n" +
                "} \n"
    private val radius = 1.0f

    override var vertexCoords: FloatArray = floatArrayOf(
        // 前面
        -1.0f, 1.0f, 1.0f,  // 0
        -1.0f, -1.0f, 1.0f,  // 1
        1.0f, -1.0f, 1.0f,  // 2
        1.0f, 1.0f, 1.0f,  // 3

        // 后面
        -1.0f, 1.0f, -1.0f,  // 4
        -1.0f, -1.0f, -1.0f,  // 5
        1.0f, -1.0f, -1.0f,  // 6
        1.0f, 1.0f, -1.0f  // 7
    )

    private val color = 1.0f
    private val vertexColors = floatArrayOf(
        color, color, color, 1.0f,
        0f, color, color, 1.0f,
        0f, 0f, color, 1.0f,
        color, 0f, color, 1f,
        color, color, 0f, 1.0f,
        0f, color, 0f, 1.0f,
        0f, 0f, 0f, 1.0f,
        color, 0f, 0f, 1.0f
    )

    private val drawOrder = byteArrayOf(
        0, 1, 2, 0, 2, 3,  // 前面
        4, 5, 6, 4, 6, 7,  // 后面
        0, 3, 7, 0, 7, 4,  // 左侧
        1, 2, 6, 1, 6, 5,  // 右侧
        0, 1, 5, 0, 5, 4,  // 顶部
        3, 2, 6, 3, 6, 7   // 底部
    )

    private var positionHandle: Int = -1
    private var colorHandle: Int = -1
    private var matrixHandle: Int = -1

    private var mRotateAngle = 0f
    private var projectionMatrix = RenderUtil.getIdentityMatrix()

    override fun initializeLocationArgs() {
        positionHandle = glGetAttribLocation(programId, "vPosition")
        colorHandle = glGetAttribLocation(programId, "vColor")
        matrixHandle = glGetUniformLocation(programId, "uMVPMatrix")
    }

    override val objType: Int
        get() = ObjType.OBJ_CUBE

    override fun onSurfaceChanged(width: Int, height: Int) {
        super.onSurfaceChanged(width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 16.0f)
    }

    override fun draw(mvpMatrix: FloatArray?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        // 丢弃之前的变换效果，构建新的 mvp 变换矩阵来覆盖
        mvpMatrixTransform()

        // Render vertex coords
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            0,
            createFloatBuffer(vertexCoords)
        )
        // set colors
        GLES30.glEnableVertexAttribArray(colorHandle)
        GLES30.glVertexAttribPointer(
            colorHandle,
            VALUE_PER_COLOR,
            GLES30.GL_FLOAT,
            false,
            0,
            createFloatBuffer(vertexColors)
        )
        // GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES,
            drawOrder.size,
            GLES30.GL_UNSIGNED_BYTE,
            createByteBuffer(drawOrder)
        )

        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(colorHandle)
    }

    private fun mvpMatrixTransform() {
        val modelMatrix = RenderUtil.getIdentityMatrix()
        val viewMatrix = RenderUtil.getIdentityMatrix()
        mRotateAngle = (mRotateAngle + 2) % 360
        Matrix.rotateM(modelMatrix, 0, mRotateAngle, 1f, 1f, 1f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 5f, 10f,
            0f, 0f, 0f, 0f, 1f, 0f)
        val mvpMatrix = FloatArray(16)
        val tempMatrix = FloatArray(16)
        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
        GLES30.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0)
    }
}