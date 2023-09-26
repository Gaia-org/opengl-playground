package com.example.opengl.samples.render

import android.opengl.GLES30
import android.util.Log
import kotlin.math.cos
import kotlin.math.sin

class CircleRenderObj : BaseRenderObj() {
    override val vertexShaderCode: String
        get() = "uniform mat4 uMVPMatrix;   \n" +
                "attribute vec4 vPosition;  \n" +
                "void main(){               \n" +
                "gl_Position = uMVPMatrix * vPosition; \n" +
                "}  \n"
    override val fragmentShaderCode: String
        get() = "precision mediump float; \n" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor; \n" +
                "} \n"
    override var vertexCoords: FloatArray = calculateCircleVertex(0.5f)

    private var positionHandle: Int = -1
    private var colorHandle: Int = -1
    private var matrixHandle: Int = -1

    override fun initializeLocationArgs() {
        positionHandle = glGetAttribLocation(programId, "vPosition")
        colorHandle = glGetUniformLocation(programId, "vColor")
        matrixHandle = glGetUniformLocation(programId, "uMVPMatrix")
    }

    override val objType: Int
        get() = ObjType.OBJ_CIRCLE

    /**
     * 默认划分为60等份并生成对应于圆上的顶点坐标
     */
    private fun calculateCircleVertex(radius: Float, granularity: Int = 60): FloatArray {
        val angleUnit = (Math.PI * 2 / granularity).toFloat()
        val circleCoords = FloatArray(granularity * 3)
        for (i in 0 until granularity) {
            circleCoords[i * 3] = (radius * cos((i * angleUnit).toDouble())).toFloat()
            circleCoords[i * 3 + 1] = (radius * sin((i * angleUnit).toDouble())).toFloat()
            circleCoords[i * 3 + 2] = 0f
        }
        Log.i("Circle", "coords: ${circleCoords.toList()}")
        return circleCoords
    }


    override fun draw(mvpMatrix: FloatArray?) {
        mvpMatrix?.let {
            GLES30.glUniformMatrix4fv(matrixHandle, 1, false, it, 0)
        }
        // 设置颜色值（彩色正方形）
        GLES30.glUniform4fv(colorHandle, 1, floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f), 0)
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
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
        GLES30.glDisableVertexAttribArray(positionHandle)
    }
}