package com.example.opengl.samples.utils

import android.opengl.GLES30
import android.util.Log

object RenderUtil {
    @JvmStatic
    fun loadShader(type: Int, shaderCode: String): Int {
        // 创建着色器对象
        val shader = GLES30.glCreateShader(type)

        // 将着色器源码加载到着色器对象中
        GLES30.glShaderSource(shader, shaderCode)

        // 编译着色器
        GLES30.glCompileShader(shader)

        // 检查编译状态
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            // 编译失败，打印错误日志并删除着色器对象
            val errorMsg = GLES30.glGetShaderInfoLog(shader)
            GLES30.glDeleteShader(shader)
            Log.e("RenderUtil", "Shader compilation failed: $errorMsg, shader code: $shaderCode")
        } else {
            Log.i("RenderUtil", "Shader compilation succeed!")
        }

        return shader
    }

    @JvmStatic
    fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
        // 创建 OpenGL ES 程序对象
        val program = GLES30.glCreateProgram()

        // 添加顶点着色器和片段着色器到程序对象中
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)

        // 链接程序对象
        GLES30.glLinkProgram(program)

        // 检查链接状态
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            // 链接失败，打印错误日志并删除程序对象
            val errorMsg = GLES30.glGetProgramInfoLog(program)
            GLES30.glDeleteProgram(program)
            throw RuntimeException("Program link failed: $errorMsg")
        }

        return program
    }
}