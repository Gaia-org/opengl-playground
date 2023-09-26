package com.example.opengl.samples.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import com.example.opengl.samples.render.TriangleRenderObj
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object RenderUtil {
    private const val TAG = "RenderUtil"

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
            Log.e(TAG, "Shader compilation failed: $errorMsg, shader code: $shaderCode")
            GLES30.glDeleteShader(shader)
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
        } else {
            Log.i(TAG, "createProgram, programId: $program")
        }

        return program
    }

    @JvmStatic
    fun createVertexBuffer(floatAttrs: FloatArray): FloatBuffer = ByteBuffer
        .allocateDirect(floatAttrs.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(floatAttrs)
            position(0)
        }

    fun getIdentityMatrix(size: Int = 16, offset: Int = 0): FloatArray {
        return FloatArray(size).also {
            Matrix.setIdentityM(it, offset)
        }
    }

    fun loadTextureFromAssets(context: Context, fileName: String): Int {
        val options = BitmapFactory.Options().apply {
            inScaled = false // 禁用缩放
        }

        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(fileName)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options) ?: return -1
            return loadTexture(bitmap)
        } catch (e: IOException) {
            throw RuntimeException("Failed to load texture from assets: $fileName", e)
        } finally {
            inputStream?.close()
        }
    }

    fun loadTexture(bitmap: Bitmap): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            throw RuntimeException("Failed to generate texture ID")
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])
//        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA,
//            bitmap.width, bitmap.height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buffer)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        // 生成MIP贴图
        //GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        bitmap.recycle()

        return textureIds[0]
    }

    fun loadTextureFromAssets(fileName: String): Int {
        val options = BitmapFactory.Options().apply {
            inScaled = false // 禁用缩放
        }

        var inputStream: InputStream? = null
        try {
            // 获取较慢，而且第一次可能获取不到https://juejin.cn/post/6844903429983174669
            inputStream = javaClass.classLoader?.getResourceAsStream(fileName)
            if (inputStream == null) {
                inputStream =  javaClass.classLoader?.getResourceAsStream(fileName)
            }
            Log.i("RenderUtil", "inputSteam: $inputStream")
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options) ?: return -1
            return loadTexture(bitmap)
        } catch (e: IOException) {
            throw RuntimeException("Failed to load texture from assets: $fileName", e)
        } finally {
            inputStream?.close()
        }
    }
}