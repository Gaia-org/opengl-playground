package com.example.opengl.samples.render

import android.opengl.GLES30
import com.example.opengl.samples.render.base.BaseRenderObj
import com.example.opengl.samples.render.base.ObjType

class SquareRenderObj : BaseRenderObj() {
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
    override var vertexCoords: FloatArray = floatArrayOf(
            -0.5f, 0.5f, 0f,  // top left
            -0.5f, -0.5f, 0f, // bottom left
            0.5f, -0.5f, 0f,  // bottom right
            0.5f, 0.5f, 0f    // top right
        )

    private val vertexColors = floatArrayOf(
        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f
    )
    // 为了避免两次定义这两个三角形共用的两个坐标，
    // 使用绘制列表告知 OpenGL ES 图形管道如何绘制这些顶点。
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    private var positionHandle: Int = -1
    private var colorHandle: Int = -1
    private var matrixHandle: Int = -1

    override fun initializeLocationArgs() {
        positionHandle = glGetAttribLocation(programId, "vPosition")
        colorHandle = glGetAttribLocation(programId, "vColor")
        matrixHandle = glGetUniformLocation(programId, "uMVPMatrix")
    }

    override val objType: Int
        get() = ObjType.OBJ_SQUARE

    override fun draw(mvpMatrix: FloatArray?) {
        mvpMatrix?.let {
            GLES30.glUniformMatrix4fv(matrixHandle, 1, false, it, 0)
        }
        // render
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
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
            GLES30.GL_UNSIGNED_SHORT,
            createShortBuffer(drawOrder)
        )

        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(colorHandle)
    }
}