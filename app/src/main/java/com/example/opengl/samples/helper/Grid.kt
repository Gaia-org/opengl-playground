package com.example.opengl.samples.helper

import android.opengl.GLES30
import com.example.opengl.samples.utils.RenderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Grid {
    private var program: Int = -1
    private lateinit var vertexBuffer: FloatBuffer

    private val vertexShaderCode =
        "#version 300 es\n" +
                "in vec4 vPosition;" +
                "uniform mat4 uMVPMatrix;\n" +
                "void main() {\n" +
                "  gl_Position = uMVPMatrix * vPosition;\n" +
                "}"
//    private val vertexShaderCode =
//        "#version 300 es\n" +
//            "layout (location = 0) in vec4 vPosition;\n" +
//            "uniform mat4 uMVPMatrix;\n" +
//            "void main() {\n" +
//            "  gl_Position = uMVPMatrix * vPosition;\n" +
//            "}"

    private val fragmentShaderCode =
        "#version 300 es\n" +
                "precision mediump float;\n" +
                "out vec4 fragColor;\n" +
                "void main() {\n" +
                "  fragColor = vec4(0.0, 1.0, 0.0, 1.0);\n" +
                "}"

    private var positionHandle: Int = -1
    private var mvpMatrixHandle: Int = -1

    private val gridVertices = floatArrayOf(
        // X轴网格
        -1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,

        // Y轴网格
        0.0f, -1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,

        // Z轴网格
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, 1.0f
    )

    private val vertexCount: Int = gridVertices.size / 3

    fun init() {
        // 编译顶点着色器和片段着色器
        val vertexShader = RenderUtil.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = RenderUtil.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // 创建 OpenGL ES 程序并连接顶点着色器和片段着色器
        program = GLES30.glCreateProgram()
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)
        GLES30.glLinkProgram(program)

        // 创建顶点缓冲区
        val bb = ByteBuffer.allocateDirect(gridVertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(gridVertices)
        vertexBuffer.position(0)

        // 获取顶点着色器中的位置句柄和 MVP 矩阵句柄
        positionHandle = GLES30.glGetAttribLocation(program, "vPosition")
        mvpMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix")
    }

    fun draw(mvpMatrix: FloatArray) {
        // 使用 OpenGL ES 程序
        GLES30.glUseProgram(program)

        // 传递顶点坐标数据
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)

        // 传递 MVP 矩阵
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // 绘制网格
        GLES30.glLineWidth(12.0f)
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, vertexCount)

        // 禁用顶点属性数组
        GLES30.glDisableVertexAttribArray(positionHandle)
    }

}